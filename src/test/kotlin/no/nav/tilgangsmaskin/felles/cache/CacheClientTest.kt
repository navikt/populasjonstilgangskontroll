package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import glide.api.GlideClient.createClient
import glide.api.models.GlideString.gs
import glide.api.models.configuration.GlideClientConfiguration
import glide.api.models.configuration.NodeAddress
import glide.api.models.configuration.StandaloneSubscriptionConfiguration
import glide.api.models.configuration.StandaloneSubscriptionConfiguration.PubSubChannelMode.EXACT
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
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.MAPPER
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.SECONDS

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@TestInstance(PER_CLASS)
@ExtendWith(MockKExtension::class)
class CacheClientTest {


    @Autowired
    lateinit var publisher: ApplicationEventPublisher
    @MockkBean
    private lateinit var token: Token

    @MockkBean
    private lateinit var cacheConfig: CacheConfig

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Autowired
    private lateinit var cf: RedisConnectionFactory
    private lateinit var lettuceClient: LettuceCacheClient
    private lateinit var glideClient: GlideCacheClient
    private lateinit var handler: CacheNøkkelHandler

    private val b1 = BrukerId("03508331575")
    private val b2 = BrukerId("20478606614")
    private val a1 = AktørId("1234567890123")
    private val a2 = AktørId("1111111111111")
    private val p1 = Person(b1,b1.verdi, a1, KommuneTilknytning(Kommune("0301")))
    private val p2 = Person(b2, b2.verdi, a2, KommuneTilknytning(Kommune("1111")))
    private val ids = setOf(p1.brukerId.verdi,p2.brukerId.verdi)

    @BeforeAll
    fun beforeAll() {
        every { cacheConfig.host} returns "host"
        every { cacheConfig.port} returns 42
        val mgr = builder(cf)
            .withInitialCacheConfigurations(mapOf(
                PDL_MED_FAMILIE_CACHE.name to RedisCacheConfiguration.defaultCacheConfig()
                    .prefixCacheNameWith(PDL)
                    .disableCachingNullValues()
            )).build().apply {
                initializeCaches()
            }
        handler = CacheNøkkelHandler(mgr.cacheConfigurations, MAPPER)
        glideClient = glideClient(handler)
        lettuceClient = lettuceClient(handler)
    }

    private fun lettuceClient(handler: CacheNøkkelHandler): LettuceCacheClient {
        val client = create("redis://${redis.host}:${redis.redisPort}")
        val lettuceClient =  LettuceCacheClient(
        client, cacheConfig,handler, BulkCacheSuksessTeller(meterRegistry, token),
        BulkCacheTeller(meterRegistry, token))
        LettuceCacheElementUtløptLytter(client,publisher)
        return lettuceClient
    }

    private fun glideClient(handler: CacheNøkkelHandler) = GlideCacheClient(createClient(GlideClientConfiguration.builder()
        .address(NodeAddress.builder()
            .host(redis.host)
            .port(redis.redisPort)
            .build())
        .subscriptionConfiguration(StandaloneSubscriptionConfiguration.builder()
            .subscription(EXACT, gs("__keyevent@0__:expired"))
            .callback(GlideCacheElementUtløptLytter(publisher))
            .build())
        .build()).get(),cacheConfig,handler)

    @BeforeEach
    fun setUp() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"
    }

    private fun cacheClients() = listOf(lettuceClient,glideClient)

    @ParameterizedTest
    @MethodSource("cacheClients")
    @DisplayName("Sletting av cache element fungerer")
    fun delete(client: CacheOperations) {
        assertThat(client.put( p1.brukerId.verdi,p1, ofSeconds(60),PDL_MED_FAMILIE_CACHE)).isTrue()
        assertThat(client.get(p1.brukerId.verdi, Person::class, PDL_MED_FAMILIE_CACHE)).isEqualTo(p1)
        assertThat(client.delete(p1.brukerId.verdi,PDL_MED_FAMILIE_CACHE)).isTrue()
        assertThat(client.get(p1.brukerId.verdi, Person::class, PDL_MED_FAMILIE_CACHE)).isNull()
    }

    @ParameterizedTest
    @MethodSource("cacheClients")
    @DisplayName("Timeout av cache element fungerer")
    fun putAndGetOne(client: CacheOperations) {
        assertThat(client.put( p1.brukerId.verdi,p1, ofSeconds(1),PDL_MED_FAMILIE_CACHE)).isTrue
        assertThat(client.get(p1.brukerId.verdi, Person::class, PDL_MED_FAMILIE_CACHE)).isEqualTo(p1)
        await.atMost(3, SECONDS).until {
            client.get(p1.brukerId.verdi, Person::class,PDL_MED_FAMILIE_CACHE) == null
        }
    }

    @ParameterizedTest
    @MethodSource("cacheClients")
     fun putAndGetMany(client: CacheOperations) {
        client.put(mapOf(p1.brukerId.verdi to p1, p2.brukerId.verdi to p2),
            ofSeconds(1),PDL_MED_FAMILIE_CACHE)
        val many = client.get(ids, Person::class,PDL_MED_FAMILIE_CACHE)
        assertThat(many.keys).containsExactlyInAnyOrderElementsOf(ids)
        await.atMost(3, SECONDS).until {
            client.get(ids, Person::class,PDL_MED_FAMILIE_CACHE).isEmpty()
        }
    }
    @ParameterizedTest
    @MethodSource("cacheClients")
    fun putOneGetTwo(client: CacheOperations) {
        client.put( p1.brukerId.verdi,p1, ofSeconds(10),PDL_MED_FAMILIE_CACHE)
        val many = client.get(ids, Person::class,PDL_MED_FAMILIE_CACHE)
        assertThat(many.keys).hasSize(1)
    }
    companion object {
       @ServiceConnection
        private val redis = RedisContainer("valkey/valkey:9")
    }
}


