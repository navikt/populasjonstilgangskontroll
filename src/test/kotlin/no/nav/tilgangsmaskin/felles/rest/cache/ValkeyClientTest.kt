package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import no.nav.tilgangsmaskin.TestApp
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
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.context.annotation.Import
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.builder
import java.time.Duration
import org.awaitility.kotlin.await
import java.util.concurrent.TimeUnit.SECONDS
import org.assertj.core.api.Assertions.assertThat

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
@Import(JacksonAutoConfiguration::class)
class ValkeyClientTest {

    private val cacheName = CacheConfig("testCache","extra")

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var valkeyMapper: ObjectMapper

    @MockkBean
    private lateinit var token: Token

    private lateinit var listener: ValkeyKeyspaceRemovalListener

    @Autowired
    private lateinit var cf: RedisConnectionFactory
    private lateinit var person1:  Person
    private lateinit var person2:  Person
    private lateinit var client: ValkeyCacheClient

    @BeforeEach
    fun setUp() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        valkeyMapper =
            objectMapper.copy().apply {
                activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
            }
        val meterRegistry = SimpleMeterRegistry()

        val mgr = builder(cf)
            .withInitialCacheConfigurations(mapOf(
                cacheName.name to RedisCacheConfiguration.defaultCacheConfig()
                    .prefixCacheNameWith("myprefix::")
                    .disableCachingNullValues()
            ))
            .build()
        mgr.getCache(cacheName.name)
        val redisClient = create("redis://${redis.host}:${redis.firstMappedPort}")
        val teller = BulkCacheTeller(meterRegistry, token)
        val handler = ValkeyCacheKeyMapper(mgr.cacheConfigurations)
        client = ValkeyCacheClient(
            redisClient,
            handler, valkeyMapper,
            BulkCacheSuksessTeller(meterRegistry, token), teller
        )
        listener = ValkeyKeyspaceRemovalListener(redisClient, handler,teller,true)
        val id1 = BrukerId("03508331575")
        val id2 = BrukerId("20478606614")
        person1 = Person(id1,id1.verdi, AktørId("1234567890123"), KommuneTilknytning(Kommune("0301")))
        person2 = Person(id2, id2.verdi, AktørId("1111111111111"), KommuneTilknytning(Kommune("1111")))
    }

    @Test
    fun putAndGetOne() {
        client.putOne(cacheName, person1.brukerId.verdi,person1, Duration.ofSeconds(1))
        val one = client.getOne<Person>(cacheName,person1.brukerId.verdi)
        assertThat(one).isEqualTo(person1)
        await.atMost(3, SECONDS).until {
            client.getOne<Person>(cacheName,person1.brukerId.verdi) == null
        }
    }
    @Test
    fun putAndGetMany() {
        every { token.system }.returns("test")
        every { token.clusterAndSystem }.returns("test:dev-gcp")

        val ids = setOf(person1.brukerId.verdi,person2.brukerId.verdi)
        client.putMany(cacheName, mapOf(person1.brukerId.verdi to person1, person2.brukerId.verdi to person2), Duration.ofSeconds(1))
        val many = client.getMany<Person>(cacheName,ids)
        assertThat(many.keys).containsExactlyInAnyOrderElementsOf(ids)
        assertThat(client.getAll(cacheName.name)).containsExactlyInAnyOrderElementsOf(ids)
        await.atMost(3, SECONDS).until {
            client.getMany<Person>(cacheName,ids).isEmpty()
        }
        await.atMost(3, SECONDS).until {
            client.getMany<Person>(cacheName,ids).isEmpty()
        }
        await.atMost(3, SECONDS).until {
            listener.fjernet.get() == 2
        }
    }

    companion object {
       @ServiceConnection
       private val redis = RedisContainer("redis:6.2.2")
    }
}