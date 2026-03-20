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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import java.util.concurrent.CopyOnWriteArrayList
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.Duration.ofSeconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@ApplyExtension(SpringExtension::class)
class CacheOperationsTest : BehaviorSpec() {

    @Autowired
    private lateinit var ctx: ConfigurableApplicationContext

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var cf: RedisConnectionFactory

    private lateinit var cache: CacheOperations
    private lateinit var redisClient: RedisClient
    private val receivedEvents = CopyOnWriteArrayList<CacheInnslagFjernetEvent>()

    private fun setUpCache() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        val mgr = builder(cf)
            .withInitialCacheConfigurations(mapOf(TEST_CACHE.name to defaultCacheConfig()
                .prefixCacheNameWith(TEST_CACHE.name)
                .disableCachingNullValues()))
            .build().also { mgr ->
                mgr.getCache(TEST_CACHE.name)
            }

        redisClient = create("redis://${redis.host}:${redis.firstMappedPort}")

        cache = CacheClient(
            redisClient, CacheNøkkelHandler(mgr.cacheConfigurations),
            BulkCacheSuksessTeller(meterRegistry, token),
            BulkCacheTeller(meterRegistry, token),
            CacheConfig("user", "pw", redis.host, redis.firstMappedPort.toString(), ofSeconds(1), ofSeconds(1))
        )
        CacheElementUtløptLytter(redisClient, ctx)
    }

    init {

        beforeSpec {
            ctx.addApplicationListener { event ->
                if (event is CacheInnslagFjernetEvent) receivedEvents.add(event)
            }
        }

        beforeEach {
            receivedEvents.clear()
            setUpCache()
        }

        given("verdier som legges i cache med kort TTL fjernes etterhvert") {
            `when`("TTL løper ut") {
                then("verdiene er borte fra cachen") {
                    putMany(T1, T2)
                    getMany(IDS).keys shouldBe IDS
                    eventually(config) {
                        getMany(IDS).shouldBeEmpty()
                    }
                }
            }
        }

        given("en eksisterende nøkkel i cachen") {
            `when`("delete kalles") {
                then("returnerer 1 og verdien er fjernet fra cachen") {
                    putOne(T1)
                    assertSoftly {
                        getOne(T1.id).shouldNotBeNull()
                        delete(T1) shouldBe 1L
                        getOne(T1.id).shouldBeNull()
                    }
                }
            }
        }

        given("en ikke-eksisterende nøkkel slettes") {
            `when`("delete kalles") {
                then("returnerer 0") {
                    assertSoftly {
                        getOne(T1.id).shouldBeNull()
                        cache.delete(TEST_CACHE, T1.id) shouldBe 0L
                    }
                }
            }
        }

        given("en nøkkel utløper i Redis") {
            `when`("TTL løper ut") {
                then("CacheInnslagFjernetEvent publiseres") {
                    putOne(T1, ofSeconds(1))
                    eventually(config) {
                        receivedEvents.any { T1.id in it.nøkkel }.shouldBeTrue()
                    }
                }
            }
        }
    }

    private fun delete(innslag: TestData) =
        cache.delete(TEST_CACHE, innslag.id)

    private fun putMany(vararg innslag: TestData, duration: Duration = ofSeconds(1)) =
        cache.putMany(TEST_CACHE, innslag.associateBy { it.id }, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(TEST_CACHE, ids, TestData::class)

    private fun putOne(innslag: TestData, duration: Duration = ofSeconds(2)) =
        cache.putOne(TEST_CACHE, innslag.id, innslag, duration)

    private fun getOne(id: String) =
        cache.getOne(TEST_CACHE, id, TestData::class)

    private companion object {
        @ServiceConnection
        val redis = RedisContainer(DEFAULT_IMAGE_NAME)
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val TEST_CACHE = CachableConfig("cache")
        val T1 = TestData.of(I1)
        val T2 = TestData.of(I2)
        val config = eventuallyConfig {
            duration = 4.seconds
            interval = 500.milliseconds
        }
    }
}
