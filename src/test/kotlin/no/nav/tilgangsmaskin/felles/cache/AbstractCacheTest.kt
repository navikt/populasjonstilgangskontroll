package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration.ofSeconds

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@Import(JacksonAutoConfiguration::class)
@ApplyExtension(SpringExtension::class)
abstract class AbstractCacheTest : DescribeSpec() {

    @Autowired
    protected lateinit var ctx: ConfigurableApplicationContext

    @Autowired
    protected lateinit var meterRegistry: MeterRegistry

    @MockkBean
    protected lateinit var token: Token

    @Autowired
    private lateinit var cf: RedisConnectionFactory

    protected lateinit var cache: CacheOperations
    protected lateinit var redisClient: RedisClient

    protected fun setUpCache() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        val mgr = builder(cf)
            .withInitialCacheConfigurations(cacheConfigurations())
            .build().also { mgr ->
                cacheConfigurations().keys.forEach { mgr.getCache(it) }
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

    protected abstract fun cacheConfigurations(): Map<String, RedisCacheConfiguration>

    protected companion object {
        @ServiceConnection
        val redis = RedisContainer(DEFAULT_IMAGE_NAME)
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val ID1 = BrukerId(I1)
        val ID2 = BrukerId(I2)
    }
}

