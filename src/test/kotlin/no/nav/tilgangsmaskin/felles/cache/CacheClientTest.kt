package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import io.lettuce.core.RedisClient.create
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule.Builder
import java.time.Duration
import java.util.concurrent.TimeUnit.*

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@TestInstance(PER_CLASS)
@ExtendWith(MockKExtension::class)
@Import(JacksonAutoConfiguration::class)
class CacheClientTest {


    private  val valkeyMapper = JsonMapper.builder().polymorphicTypeValidator(NavPolymorphicTypeValidator()).apply {
        addModule(Builder().build())
        addModule(JacksonTypeInfoAddingValkeyModule())
    }.build()


    @Autowired
    private lateinit var meterRegistry:  MeterRegistry

    @MockkBean
    private lateinit var token: Token

    @Autowired
    lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var listener: CacheElementUtløptLytter

    @Autowired
    private lateinit var cf: RedisConnectionFactory
    private lateinit var person1:  Person
    private lateinit var person2:  Person
    private lateinit var client: CacheClient

    @BeforeEach
    fun setUp() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        val mgr = builder(cf)
            .withInitialCacheConfigurations(mapOf(
                PDL_MED_FAMILIE_CACHE.name to RedisCacheConfiguration.defaultCacheConfig()
                    .prefixCacheNameWith(PDL)
                    .disableCachingNullValues()
            ))
            .build()
        mgr.getCache(PDL_MED_FAMILIE_CACHE.name)
        val redisClient = create("redis://${redis.host}:${redis.firstMappedPort}")
        val teller = BulkCacheTeller(meterRegistry, token)
        val handler = CacheNøkkelHandler(mgr.cacheConfigurations, valkeyMapper)
        client = CacheClient(
            redisClient, handler, BulkCacheSuksessTeller(meterRegistry, token), teller
        )
        listener = CacheElementUtløptLytter(redisClient, eventPublisher)
        val id1 = BrukerId("03508331575")
        val id2 = BrukerId("20478606614")
        person1 = Person(id1,id1.verdi, AktørId("1234567890123"), KommuneTilknytning(Kommune("0301")))
        person2 = Person(id2, id2.verdi, AktørId("1111111111111"), KommuneTilknytning(Kommune("1111")))
    }

    @Test
    fun putAndGetOnePdl() {
        client.putOne(person1.brukerId.verdi, PDL_MED_FAMILIE_CACHE, person1, Duration.ofSeconds(1))
        val one = client.getOne(person1.brukerId.verdi, PDL_MED_FAMILIE_CACHE, Person::class)
        assertThat(one).isEqualTo(person1)
        await.atMost(3, SECONDS).until {
            client.getOne(person1.brukerId.verdi, PDL_MED_FAMILIE_CACHE, Person::class) == null
        }
    }
    @Test
    fun putAndGetManyPdl() {
        val ids = setOf(person1.brukerId.verdi,person2.brukerId.verdi)
        client.putMany(mapOf(person1.brukerId.verdi to person1, person2.brukerId.verdi to person2),
            PDL_MED_FAMILIE_CACHE,
            Duration.ofSeconds(1))
        val many = client.getMany(ids, PDL_MED_FAMILIE_CACHE, Person::class)
        assertThat(many.keys).containsExactlyInAnyOrderElementsOf(ids)
        val nøkler = client.getAllKeys(PDL_MED_FAMILIE_CACHE).map { CacheNøkkelElementer(it).id }
        assertThat(nøkler).containsExactlyInAnyOrderElementsOf(ids)
        await.atMost(3, SECONDS).until {
            client.getMany(ids, PDL_MED_FAMILIE_CACHE, Person::class).isEmpty()
        }
    }

    companion object {
       @ServiceConnection
       private val redis = RedisContainer("redis:6.2.2")
    }
}
