package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import io.lettuce.core.RedisClient
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.context.annotation.Import
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import kotlin.test.assertEquals

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
@Import(JacksonAutoConfiguration::class)
class ValkeyServerTest {

    private val cacheName = CacheConfig("testCache")

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var valkeyMapper: ObjectMapper

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var cf: RedisConnectionFactory

    private lateinit var client: ValkeyCacheClient

    @BeforeEach
    fun setUp() {
        valkeyMapper =
            objectMapper.copy().apply {
                activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
            }
        val meterRegistry = SimpleMeterRegistry()
        val redisClient = RedisClient.create("redis://${redis.host}:${redis.firstMappedPort}")
        val customConfig = RedisCacheConfiguration.defaultCacheConfig()
            .prefixCacheNameWith("myprefix::")
            .disableCachingNullValues()

        val cacheConfigs = mapOf(cacheName.name to customConfig)
        val mgr = RedisCacheManager.builder(cf)
            .withInitialCacheConfigurations(cacheConfigs)
            .build()
        mgr.getCache(cacheName.name)
        client = ValkeyCacheClient(ValkeyCacheKeyHandler(mgr.cacheConfigurations),redisClient.connect(), valkeyMapper,
            BulkCacheSuksessTeller(meterRegistry, token),
            BulkCacheTeller(meterRegistry,token)
        )
    }

    @Test
    fun putAndGet() {
        val id = BrukerId("03016536325")
        val person = Person(id, AktørId("1234567890123"), GeografiskTilknytning.KommuneTilknytning(
            GeografiskTilknytning.Kommune("0301")))
        client.putOne(cacheName, id.verdi,person)
        val one = client.getOne<Person>(cacheName,id.verdi)
        assertEquals(person, one)
    }

    companion object {
       @ServiceConnection
       private val redis = RedisContainer("redis:6.2.2")
    }
}