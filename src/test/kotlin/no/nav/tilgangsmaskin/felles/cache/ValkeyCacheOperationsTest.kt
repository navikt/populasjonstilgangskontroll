package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetHendelse
import java.util.concurrent.CopyOnWriteArrayList
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.ValkeyCacheOperationsTest.ValkeyCacheTestConfig
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils

import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import io.kotest.matchers.comparables.shouldBeLessThan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.MOR
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT
import org.springframework.context.annotation.Primary
import java.time.Duration.ofSeconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@DataRedisTest
@AutoConfigureMetrics
@Import(ValkeyCacheTestConfig::class)
@ApplyExtension(SpringExtension::class)
class ValkeyCacheOperationsTest : BehaviorSpec() {

    @TestConfiguration
    class ValkeyCacheTestConfig(private val cf: RedisConnectionFactory) {

        @Bean
        @Primary
        fun cacheConfig() =
            CacheConfig("unused", "unused", redis.host, redis.firstMappedPort, ofSeconds(5))

        @Bean
        fun redisClient(cfg: CacheConfig) =
            create("redis://${cfg.host}:${cfg.port}")

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
        fun cacheNøkkelHandler(mgr: RedisCacheManager) =
            CacheNøkkelMapper(mgr.cacheConfigurations)

        @Bean
        fun bulkCacheSuksessTeller(meterRegistry: MeterRegistry, token: Token) =
            BulkCacheSuksessTeller(meterRegistry, token)

        @Bean
        fun bulkCacheTeller(meterRegistry: MeterRegistry, token: Token) =
            BulkCacheTeller(meterRegistry, token)

        @Bean
        fun valkeyCacheOperations(client: RedisClient, handler: CacheNøkkelMapper, cfg: CacheConfig, alle: BulkCacheSuksessTeller, bulk: BulkCacheTeller) =
            ValkeyCacheOperations(client, handler, alle, bulk, cfg)

        @Bean
        fun cacheElementUtløptLytter(client: RedisClient, publisher: ApplicationEventPublisher) =
            CacheElementUtløptLytter(client, publisher)
    }

    @Autowired
    private lateinit var ctx: ConfigurableApplicationContext

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var cache: CacheOperations


    private val events = CopyOnWriteArrayList<CacheInnslagFjernetHendelse>()

    init {

        beforeSpec {
            ctx.addApplicationListener { event ->
                if (event is CacheInnslagFjernetHendelse) events.add(event)
            }
        }

        beforeEach {
            every { token.system } returns "test"
            every { token.clusterAndSystem } returns "test:dev-gcp"
            events.clear()
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
                Then("returnerer 1 og verdien er fjernet") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                    assertSoftly {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1) shouldBe P1
                        cache.delete(PDL_MED_FAMILIE_CACHE, I1) shouldBe 1L
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1).shouldBeNull()
                    }
                }
            }
            When("nøkkelen ikke eksisterer") {
                Then("returnerer 0") {
                    assertSoftly {
                        cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, I1).shouldBeNull()
                        cache.delete(PDL_MED_FAMILIE_CACHE, I1) shouldBe 0
                    }
                }
            }
        }

        Given("cache-utløp") {
            When("TTL løper ut") {
                Then("CacheInnslagFjernetHendelse publiseres") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(1))
                    eventually(TIMEOUTS) {
                        events.any { I1 in it.nøkkel }.shouldBeTrue()
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
                every { ClusterUtils.isProd } returns true
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
            When("getOne kalles mot pauset Redis") {
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
            When("getMany kalles mot pauset Redis") {
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
            When("putOne kalles mot pauset Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany kalles mot pauset Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany feiler mot pauset Redis og Redis gjenopprettes") {
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
                        val entries = chunk.associate { "id-$it" to Person(
                            brukerId = BrukerId("%011d".format(it)),
                            aktørId = AktørId("%013d".format(it)),
                            geoTilknytning = UkjentBosted()
                        ) }
                        cache.putMany(PDL_MED_FAMILIE_CACHE, entries, ofSeconds(60))
                    }

                    val elapsed = measureTime {
                        cache.size(PDL_MED_FAMILIE_CACHE) shouldBe 5000
                    }
                    elapsed shouldBeLessThan 5.seconds

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
                        aktørId = AktørId(AKTØR_ID),
                        geoTilknytning = KommuneTilknytning(Kommune("0301")),
                        graderinger = listOf(UGRADERT),
                        familie = Familie(
                            foreldre = setOf(FamilieMedlem(BrukerId(I2), MOR))
                        ),
                        historiskeIds = setOf(BrukerId(I2))
                    )

                    cache.putOne(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi, person, ofSeconds(5))
                    val retrieved = cache.getOne<Person>(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi)

assertSoftly(retrieved!!) {
    this shouldBe person
    brukerId.verdi shouldBe I1
    aktørId.verdi shouldBe AKTØR_ID
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
                    val result = cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, IDS)

                    assertSoftly {
                        result[I1] shouldBe P1
                        result[I2] shouldBe P2
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
        private const val AKTØR_ID = "1234567890123"
        private const val AKTØR_ID_2 = "9876543210123"
        private val IDS = setOf(I1, I2)
        private val P1 = Person(BrukerId(I1), I1, AktørId(AKTØR_ID), BydelTilknytning(Bydel("030101")), listOf(FORTROLIG))
        private val P2 = Person(BrukerId(I2), I2, AktørId(AKTØR_ID_2), UkjentBosted())
        private val TIMEOUTS = eventuallyConfig {
            duration = 2.seconds
            interval = 100.milliseconds
        }
    }
}
