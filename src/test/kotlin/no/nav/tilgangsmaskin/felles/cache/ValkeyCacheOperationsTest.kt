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
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
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
                    mapOf(TEST_CACHE.name to defaultCacheConfig()
                        .prefixCacheNameWith(TEST_CACHE.name)
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
        fun valkeyCacheClient(client: RedisClient, handler: CacheNøkkelMapper, cfg: CacheConfig, alle: BulkCacheSuksessTeller, bulk: BulkCacheTeller) =
            ValkeyCacheClient(client, handler, alle, bulk, cfg)

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
            cache.clear(TEST_CACHE)
        }

        Given("putMany og getMany") {
            When("verdier legges i cache med kort TTL") {
                Then("returneres ved oppslag og fjernes etter TTL") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(1))
                    val many = cache.getMany<TestData>(TEST_CACHE, IDS)
                    many.keys shouldBe IDS
                    many.values shouldBe listOf(T1, T2)
                    eventually(TIMEOUTS) {
                        cache.getMany<TestData>(TEST_CACHE, IDS).shouldBeEmpty()
                    }
                }
            }
            When("kalles med tomt set") {
                Then("returnerer tomt map") {
                    cache.getMany<TestData>(TEST_CACHE, emptySet()).shouldBeEmpty()
                }
            }
        }

        Given("sletting av enkeltinnslag") {
            When("nøkkelen eksisterer") {
                Then("returnerer 1 og verdien er fjernet") {
                    cache.putOne(TEST_CACHE, T1.id, T1, ofSeconds(2))
                    assertSoftly {
                        cache.getOne<TestData>(TEST_CACHE, T1.id) shouldBe T1
                        cache.delete(TEST_CACHE, T1.id) shouldBe 1L
                        cache.getOne<TestData>(TEST_CACHE, T1.id).shouldBeNull()
                    }
                }
            }
            When("nøkkelen ikke eksisterer") {
                Then("returnerer 0") {
                    assertSoftly {
                        cache.getOne<TestData>(TEST_CACHE, T1.id).shouldBeNull()
                        cache.delete(TEST_CACHE, T1.id) shouldBe 0
                    }
                }
            }
        }

        Given("cache-utløp") {
            When("TTL løper ut") {
                Then("CacheInnslagFjernetHendelse publiseres") {
                    cache.putOne(TEST_CACHE, T1.id, T1, ofSeconds(1))
                    eventually(TIMEOUTS) {
                        events.any { T1.id in it.nøkkel }.shouldBeTrue()
                    }
                }
            }
        }

        Given("tømming av cache") {
            When("cache inneholder verdier") {
                Then("alle verdier i cachen fjernes") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(1))
                    cache.getMany<TestData>(TEST_CACHE, IDS).keys shouldBe IDS
                    cache.clear(TEST_CACHE)
                    cache.getMany<TestData>(TEST_CACHE, IDS).shouldBeEmpty()
                }
            }
            When("cache er tom") {
                Then("clear kaster ikke exception") {
                    cache.clear(TEST_CACHE)
                    cache.getMany<TestData>(TEST_CACHE, IDS).shouldBeEmpty()
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
                    shouldThrow<IllegalStateException> { cache.clear(TEST_CACHE) }
                        .message shouldContain "prod"
                }
            }
        }

        Given("graceful degradation ved Redis-feil") {
            When("getOne kalles mot pauset Redis") {
                Then("returnerer null i stedet for å kaste exception") {
                    cache.putOne(TEST_CACHE, T1.id, T1, ofSeconds(30))
                    cache.getOne<TestData>(TEST_CACHE, T1.id) shouldBe T1
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.getOne<TestData>(TEST_CACHE, T1.id).shouldBeNull()
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("getMany kalles mot pauset Redis") {
                Then("returnerer tomt map i stedet for å kaste exception") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(30))
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.getMany<TestData>(TEST_CACHE, IDS).shouldBeEmpty()
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putOne kalles mot pauset Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putOne(TEST_CACHE, T1.id, T1, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany kalles mot pauset Redis") {
                Then("kaster ikke exception") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                }
            }
            When("putMany feiler mot pauset Redis og Redis gjenopprettes") {
                Then("tilkoblingen er fortsatt brukbar og neste putMany lagrer riktig") {
                    redis.dockerClient.pauseContainerCmd(redis.containerId).exec()
                    try {
                        cache.putMany(TEST_CACHE, arrayOf(T1).associateBy { it.id }, ofSeconds(30))
                    } finally {
                        redis.dockerClient.unpauseContainerCmd(redis.containerId).exec()
                    }
                    Thread.sleep(200)
                    cache.putMany(TEST_CACHE, arrayOf(T2).associateBy { it.id }, ofSeconds(30))
                    eventually(TIMEOUTS) {
                        cache.getOne<TestData>(TEST_CACHE, T2.id) shouldBe T2
                    }
                }
            }
        }

        Given("antall innslag i cache") {
            When("cache er tom") {
                Then("returnerer 0") {
                    cache.size(TEST_CACHE) shouldBe 0
                }
            }
            When("cache inneholder verdier") {
                Then("returnerer antall innslag") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(5))
                    cache.size(TEST_CACHE) shouldBe 2
                }
            }
            When("verdier fjernes") {
                Then("size oppdateres") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(5))
                    cache.size(TEST_CACHE) shouldBe 2
                    cache.delete(TEST_CACHE, T1.id)
                    cache.size(TEST_CACHE) shouldBe 1
                }
            }
            When("clear kalles") {
                Then("size blir 0") {
                    cache.putMany(TEST_CACHE, arrayOf(T1, T2).associateBy { it.id }, ofSeconds(5))
                    cache.size(TEST_CACHE) shouldBe 2
                    cache.clear(TEST_CACHE)
                    cache.size(TEST_CACHE) shouldBe 0
                }
            }
            When("50000 innslag legges inn") {
                Then("size returnerer 50000 og clear tømmer alt") {
                    val batchSize = 50_000
                    (1..50000).chunked(batchSize).forEach { chunk ->
                        val entries = chunk.associate { "id-$it" to TestData.of("id-$it") }
                        cache.putMany(TEST_CACHE, entries, ofSeconds(60))
                    }

                    val elapsed = measureTime {
                        cache.size(TEST_CACHE) shouldBe 50000
                    }
                    elapsed shouldBeLessThan 5.seconds

                    cache.clear(TEST_CACHE)
                    cache.size(TEST_CACHE) shouldBe 0
                }
            }
        }
        Given("serialisering av sealed class-hierarkier") {
            When("GeografiskTilknytning-subtyper lagres og hentes") {
                Then("korrekt subtype deserialiseres") {
                    val kommune = KommuneTilknytning(Kommune("0301"))
                    val bydel = BydelTilknytning(Bydel("030101"))
                    val ukjent = UkjentBosted()
                    val utland = UtenlandskTilknytning()
                    val udefinert = UdefinertTilknytning()

                    cache.putOne(TEST_CACHE, "kommune", kommune, ofSeconds(5))
                    cache.putOne(TEST_CACHE, "bydel", bydel, ofSeconds(5))
                    cache.putOne(TEST_CACHE, "ukjent", ukjent, ofSeconds(5))
                    cache.putOne(TEST_CACHE, "utland", utland, ofSeconds(5))
                    cache.putOne(TEST_CACHE, "udefinert", udefinert, ofSeconds(5))

                    assertSoftly {
                        cache.getOne<GeografiskTilknytning>(TEST_CACHE, "kommune") shouldBe kommune
                        cache.getOne<GeografiskTilknytning>(TEST_CACHE, "bydel") shouldBe bydel
                        cache.getOne<GeografiskTilknytning>(TEST_CACHE, "ukjent") shouldBe ukjent
                        cache.getOne<GeografiskTilknytning>(TEST_CACHE, "utland") shouldBe utland
                        cache.getOne<GeografiskTilknytning>(TEST_CACHE, "udefinert") shouldBe udefinert
                    }
                }
            }
            When("GeografiskTilknytning lagres via getMany/putMany") {
                Then("korrekte subtyper returneres") {
                    val entries = mapOf(
                        "k1" to KommuneTilknytning(Kommune("0301")),
                        "k2" to BydelTilknytning(Bydel("030101")),
                        "k3" to UkjentBosted()
                    )
                    cache.putMany(TEST_CACHE, entries, ofSeconds(5))
                    val result = cache.getMany<GeografiskTilknytning>(TEST_CACHE, entries.keys)

                    assertSoftly {
                        result["k1"] shouldBe entries["k1"]
                        result["k2"] shouldBe entries["k2"]
                        result["k3"] shouldBe entries["k3"]
                    }
                }
            }
        }

    }

    private companion object {
        @ServiceConnection
        val redis = RedisContainer(DEFAULT_IMAGE_NAME)
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val TEST_CACHE = CacheNøkkelConfig("cache", "jalla")
        val T1 = TestData.of(I1)
        val T2 = TestData.of(I2)
        val TIMEOUTS = eventuallyConfig {
            duration = 4.seconds
            interval = 500.milliseconds
        }

        private data class TestData(val id: String, val navn: String, val alder: Int, val kontakt: Kontakt) {
            data class Kontakt(val epost: String, val telefon: String, val adresse: Adresse) {
                data class Adresse(val gate: String, val postnummer: String, val by: String)
            }
            companion object {
                fun of(id: String) = TestData(
                    id, "Navn $id", 42,
                    Kontakt("$id@test.no", "99887766", Kontakt.Adresse("Testgata 1", "0001", "Oslo"))
                )
            }
        }
    }
}
