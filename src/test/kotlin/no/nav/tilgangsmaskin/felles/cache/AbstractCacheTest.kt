package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration.ofSeconds

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@TestInstance(PER_CLASS)
@ExtendWith(MockKExtension::class)
@Import(JacksonAutoConfiguration::class)
abstract class AbstractCacheTest {

    @Autowired
    protected lateinit var meterRegistry: MeterRegistry

    @MockkBean
    protected lateinit var token: Token

    @Autowired
    private lateinit var cf: RedisConnectionFactory

    protected lateinit var cache: CacheOperations
    protected lateinit var redisClient: RedisClient

    protected lateinit var mockServer: MockRestServiceServer


    @BeforeEach
    fun setUpCache() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        val mgr = builder(cf)
            .withInitialCacheConfigurations(cacheConfigurations())
            .build().also { mgr ->
                cacheConfigurations().keys.forEach { mgr.getCache(it) } // init
            }

        redisClient = create("redis://${redis.host}:${redis.firstMappedPort}")
        val handler = CacheNøkkelHandler(mgr.cacheConfigurations)

        cache = CacheClient(
            redisClient, handler,
            BulkCacheSuksessTeller(meterRegistry, token),
            BulkCacheTeller(meterRegistry, token),
            CacheConfig("user", "pw", redis.host, redis.firstMappedPort.toString(), ofSeconds(1), ofSeconds(1))
        )
    }

    protected abstract fun cacheConfigurations(): Map<String, RedisCacheConfiguration>

    protected companion object {
        @ServiceConnection
        val redis = RedisContainer(DEFAULT_IMAGE_NAME)
        protected const val I1 = "03508331575"
        protected const val I2 = "20478606614"
        @JvmStatic
        protected val IDS = setOf(I1, I2)
        @JvmStatic
        protected val ID1 = BrukerId(I1)
        @JvmStatic
        protected val ID2 = BrukerId(I2)
    }
}

