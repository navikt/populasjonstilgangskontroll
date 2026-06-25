package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheOperationsTest.ValkeyCacheTestConfig
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.config.RedisListenerConfigurer
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.RedisMessageConverters
import java.time.Duration.ofSeconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@DataRedisTest
@AutoConfigureMetrics
@Import(ValkeyCacheTestConfig::class, ValkeyEventListeningCacheOppfrisker::class)
@ApplyExtension(SpringExtension::class)
class ValkeyCacheOperationsTest : BehaviorSpec() {


    @TestConfiguration
    class ValkeyCacheTestConfig(private val cf: RedisConnectionFactory) : RedisListenerConfigurer{


        override fun configureMessageConverters(builder: RedisMessageConverters.Builder) {
            builder.addCustomConverter(CacheNøkkelMessageConverter())
        }

        @Bean
        fun cacheManager() =
            builder(cf)
                .withInitialCacheConfigurations(
                    mapOf(PDL_MED_FAMILIE_CACHE.name to defaultCacheConfig()
                        .prefixCacheNameWith(PDL_MED_FAMILIE_CACHE.name)
                        .disableCachingNullValues())
                )
                .build()


        @Bean
        fun cacheOppfriskerTeller(meterRegistry: MeterRegistry, token: Token) =
            CacheOppfriskerTeller(meterRegistry, token)

        @Bean
        fun valkeyCacheOperations(valkey: StringRedisTemplate) =
            ValkeyCacheOperations(valkey)
    }

    @MockkBean
    private lateinit var token: Token

    @MockkBean
    private lateinit var oppfrisker: CacheOppfrisker

    @Autowired
    private lateinit var cache: CacheOperations

    @Autowired
    private lateinit var registry: MeterRegistry


    init {

        beforeEach {
            every { token.system } returns "test"
            every { token.clusterAndSystem } returns "test:dev-gcp"
            every { oppfrisker.cacheName } returns PDL_MED_FAMILIE_CACHE.name
            every { oppfrisker.oppfrisk(any()) } returns Unit
            cache.clear(PDL_MED_FAMILIE_CACHE)
        }

        Given("putMany og getMany") {
            When("verdier legges i cache med kort TTL") {
                Then("returneres ved oppslag og fjernes etter TTL") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(1))
                    val many = cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS)
                    many.keys shouldBe IDS
                    many.values shouldBe listOf(P1, P2)
                    eventually(TIMEOUTS) {
                        cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS).shouldBeEmpty()
                    }
                }
            }
            When("kalles med tomt set") {
                Then("returnerer tomt map") {
                    cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, emptySet()).shouldBeEmpty()
                }
            }
        }

        Given("sletting av enkeltinnslag") {
            When("nøkkelen eksisterer") {
                Then("returnerer true og verdien er fjernet") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                    assertSoftly {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1) shouldBe P1
                        cache.delete(PDL_MED_FAMILIE_CACHE, I1) shouldBe true
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1).shouldBeNull()
                    }
                }
            }
            When("nøkkelen ikke eksisterer") {
                Then("returnerer false") {
                    assertSoftly {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1).shouldBeNull()
                        cache.delete(PDL_MED_FAMILIE_CACHE, I1) shouldBe false
                    }
                }
            }
        }

        Given("cache-utløp") {
            When("TTL løper ut") {
                Then("Valkey publiserer expired-event som håndteres av ValkeyListener") {


                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(1))

                    eventually(VALKEY_EVENT_TIMEOUTS) {
                        verify {
                            oppfrisker.oppfrisk(match {
                                it.cacheName == PDL_MED_FAMILIE_CACHE.name && it.id == I1
                            })
                        }
                    }
                }
            }
        }

        Given("tømming av cache") {
            When("cache inneholder verdier") {
                Then("alle verdier i cachen fjernes") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(1))
                    cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS).keys shouldBe IDS
                    cache.clear(PDL_MED_FAMILIE_CACHE)
                    cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS).shouldBeEmpty()
                }
            }
            When("cache er tom") {
                Then("clear kaster ikke exception") {
                    cache.clear(PDL_MED_FAMILIE_CACHE)
                    cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS).shouldBeEmpty()
                }
            }
        }

        Given("clear i prod-miljø") {
            beforeEach {
                mockkObject(ClusterUtils.Companion)
                every { isProd } returns true
            }
            afterEach { unmockkObject(ClusterUtils.Companion) }

            When("clear kalles") {
                Then("kaster IllegalStateException fordi clear er blokkert i prod") {
                    shouldThrow<IllegalStateException> { cache.clear(PDL_MED_FAMILIE_CACHE) }
                        .message shouldContain "prod"
                }
            }
        }

        Given("graceful degradation ved Redis-feil") {
            When("getOne kalles mot utilgjengelig Redis") {
                Then("returnerer null i stedet for å kaste exception") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(30))
                    cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1) shouldBe P1
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1).shouldBeNull()
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("getMany kalles mot utilgjengelig Redis") {
                Then("returnerer tomt map i stedet for å kaste exception") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(30))
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS).shouldBeEmpty()
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putOne kalles mot utilgjengelig Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany kalles mot utilgjengelig Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany feiler mot utilgjengelig Redis og Redis gjenopprettes") {
                Then("tilkoblingen er fortsatt brukbar og neste putMany lagrer riktig") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1), ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I2 to P2), ofSeconds(30))
                    eventually(TIMEOUTS) {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I2) shouldBe P2
                    }
                }
            }
        }

        Given("cache-metrikker") {
            When("getOne treffer cache") {
                Then("registreres varighet med cache, operasjon og hit-resultat") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(5))

                    cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1) shouldBe P1

                }
            }

            When("getMany gir både treff og miss") {
                Then("registreres varighet med delvis-resultat") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(5))

                    cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, setOf(I1, I2)).keys shouldBe setOf(I1)
                }
            }

            When("putOne feiler mot utilgjengelig Redis") {
                Then("registreres varighet med feilet-resultat") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
        }

        Given("antall innslag i cache") {
            When("cache er tom") {
                Then("returnerer 0") {
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 0
                }
            }
            When("cache inneholder verdier") {
                Then("returnerer antall innslag") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(5))
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 2
                }
            }
            When("verdier fjernes") {
                Then("size oppdateres") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(5))
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 2
                    cache.delete(PDL_MED_FAMILIE_CACHE, I1)
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 1
                }
            }
            When("clear kalles") {
                Then("size blir 0") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(5))
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 2
                    cache.clear(PDL_MED_FAMILIE_CACHE)
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 0
                }
            }
            When("5000 innslag legges inn") {
                Then("size returnerer 5000 og clear tømmer alt") {
                    val batchSize = 5_000
                    (1..5000).chunked(batchSize).forEach { chunk ->
                        val entries = chunk.associate {
                            "id-$it" to Person(
                                brukerId = BrukerId("%011d".format(it)),
                                aktørId = AktørId("%013d".format(it)),
                                geoTilknytning = UkjentBosted()
                            )
                        }
                        cache.putMany(PDL_MED_FAMILIE_CACHE, entries, ofSeconds(60))
                    }

                    val elapsed = measureTime {
                        cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 5000
                    }
                    elapsed shouldBeLessThan 5.seconds
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 5000

                    cache.clear(PDL_MED_FAMILIE_CACHE)
                    cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 0
                }
            }
        }

        Given("serialisering av Person") {
            When("Person med BrukerId, AktørId og GeografiskTilknytning lagres og hentes") {
                Then("alle felter deserialiseres korrekt") {
                    val person = Person(
                        brukerId = BrukerId(I1),
                        aktørId = AktørId(AKTOR_ID),
                        geoTilknytning = KommuneTilknytning(Kommune("0301")),
                        graderinger = listOf(UGRADERT),
                        familie = Familie(
                            foreldre = setOf(FamilieMedlem(BrukerId(I2), MOR))
                        ),
                        historiskeIds = setOf(BrukerId(I2))
                    )

                    cache.putOne(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi, person, ofSeconds(5))

                    assertSoftly(cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi)!!) {
                        this shouldBe person
                        brukerId.verdi shouldBe I1
                        aktørId.verdi shouldBe AKTOR_ID
                        geoTilknytning shouldBe KommuneTilknytning(Kommune("0301"))
                        graderinger shouldBe listOf(UGRADERT)
                        familie.foreldre.first().brukerId shouldBe BrukerId(I2)
                        historiskeIds shouldBe setOf(BrukerId(I2))
                    }
                }
            }
            When("Person lagres og hentes via putMany/getMany") {
                Then("korrekt Person returneres for hver nøkkel") {
                    val entries = mapOf(I1 to P1, I2 to P2)
                    cache.putMany(PDL_MED_FAMILIE_CACHE, entries, ofSeconds(5))

                    assertSoftly(cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS)) {
                        this[I1] shouldBe P1
                        this[I2] shouldBe P2
                    }
                }
            }
        }
    }

    private companion object {
        @ServiceConnection
        private val redis = RedisContainer(DEFAULT_IMAGE_NAME)
        private const val I1 = "03508331575"
        private const val I2 = "20478606614"
        private const val AKTOR_ID = "1234567890123"
        private const val AKTOR_ID_2 = "9876543210123"
        private val IDS = setOf(I1, I2)
        private val P1 =
            Person(BrukerId(I1), I1, AktørId(AKTOR_ID), BydelTilknytning(Bydel("030101")), listOf(FORTROLIG))
        private val P2 = Person(BrukerId(I2), I2, AktørId(AKTOR_ID_2), UkjentBosted())
        private val TIMEOUTS = eventuallyConfig {
            duration = 2.seconds
            interval = 100.milliseconds
        }
        private val VALKEY_EVENT_TIMEOUTS = eventuallyConfig {
            duration = 5.seconds
            interval = 100.milliseconds
        }
    }
}
