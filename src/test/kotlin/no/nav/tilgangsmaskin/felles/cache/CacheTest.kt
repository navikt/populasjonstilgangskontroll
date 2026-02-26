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
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.net.URI
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.json.JsonMapper
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.*

@DataRedisTest
@ContextConfiguration(classes = [TestApp::class])
@Testcontainers
@AutoConfigureMetrics
@TestInstance(PER_CLASS)
@ExtendWith(MockKExtension::class)
@Import(JacksonAutoConfiguration::class)
class CacheTest {

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Autowired
    private lateinit var mapper: JsonMapper

    @MockkBean
    private lateinit var token: Token

    @Autowired
    lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var listener: CacheElementUtløptLytter

    @Autowired
    private lateinit var cf: RedisConnectionFactory
    private lateinit var cache: CacheOperations

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
            .build().apply {
                getCache(PDL_MED_FAMILIE_CACHE.name) // init
            }
        val redisClient = create("redis://${redis.host}:${redis.firstMappedPort}")
        val handler = CacheNøkkelHandler(mgr.cacheConfigurations)

        cache = CacheClient(
            redisClient, handler, BulkCacheSuksessTeller(meterRegistry, token),
            BulkCacheTeller(meterRegistry, token), CacheConfig("user","pw",redis.host, redis.firstMappedPort.toString(),
                ofSeconds(1),
                ofSeconds(1))
        )

        listener = CacheElementUtløptLytter(redisClient, eventPublisher)

    }

    @Test
    @DisplayName("Put og get en verdi, og verifiser at den er borte etter utløp")
    fun putAndGetOne() {
        cache.putOne(I1, PDL_MED_FAMILIE_CACHE, P1, ofSeconds(1))
        val one = cache.getOne(I1, PDL_MED_FAMILIE_CACHE, Person::class)
        assertThat(one).isEqualTo(P1)
        await.atMost(3, SECONDS).until {
            cache.getOne(I1, PDL_MED_FAMILIE_CACHE, Person::class) == null
        }
    }
    @Test
    @DisplayName("Put og get flere verdier, og verifiser at de er borte etter utløp")
    fun putAndGetMany() {
        cache.putMany(mapOf(I1 to P1, I2 to P2),
            PDL_MED_FAMILIE_CACHE,
            ofSeconds(1))
        val many = cache.getMany(IDS, PDL_MED_FAMILIE_CACHE, Person::class)
        assertThat(many.keys).containsExactlyInAnyOrderElementsOf(IDS)
        assertThat(P1).isEqualTo(cache.getOne(I1,PDL_MED_FAMILIE_CACHE,Person::class))

        assertThat(P2).isEqualTo(cache.getOne(I2,PDL_MED_FAMILIE_CACHE,Person::class))

        await.atMost(3, SECONDS).until {
            cache.getMany(IDS, PDL_MED_FAMILIE_CACHE, Person::class).isEmpty()
        }
    }

    @Test
    @DisplayName("Hent personer fra cache og rest, og verifiser at cache treffer og at rest treffes ved cache miss")
    fun henterBareCacheMissFraRest() {
        val baseUri = URI.create("http://pdl")
        val restClientBuilder = RestClient.builder().baseUrl("$baseUri")
        val mockServer = bindTo(restClientBuilder).build()
        val cfg = PdlConfig(baseUri)
        val adapter = PdlRestClientAdapter(restClientBuilder.build(), cfg, cache, mapper)


        val restRespons = mapper.writeValueAsString(mapOf(I2 to PdlRespons(
            PdlPerson(),
            PdlIdenter(listOf(
                PdlIdent(I2, false, FOLKEREGISTERIDENT),
                PdlIdent(A2.verdi, false, AKTORID)
            )),
            PdlGeografiskTilknytning(KOMMUNE, GTKommune((P2.geoTilknytning as KommuneTilknytning).kommune.verdi))
        )))

        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons, APPLICATION_JSON))

        cache.putOne(I1, PDL_MED_FAMILIE_CACHE, P1, ofSeconds(10))
        val personer = adapter.personer(IDS)

        mockServer.verify()
        assertThat(personer).containsExactlyInAnyOrder(P1,P2)
        assertThat(cache.getOne(I1,PDL_MED_FAMILIE_CACHE,Person::class)).isEqualTo(P1)
        assertThat(cache.getOne(I2,PDL_MED_FAMILIE_CACHE,Person::class)).isEqualTo(P2)

        mockServer.reset()
        assertThat(adapter.personer(IDS)).containsExactlyInAnyOrder(P1,P2)
        mockServer.verify()

        mockServer.reset()
        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons, APPLICATION_JSON))
        cache.delete(PDL_MED_FAMILIE_CACHE,I2)
        assertThat(adapter.personer(setOf(I2))).containsExactly(P2)
        mockServer.verify()
        assertThat(cache.getOne(I2,PDL_MED_FAMILIE_CACHE,Person::class)).isEqualTo(P2)


    }

    companion object {
        private val A1 = AktørId("1234567890123")
        private val A2 = AktørId("1234567890123")
        private const val I1 = "03508331575"
        private const val I2 = "20478606614"
        private val IDS = setOf(I1, I2)
        private val ID1 = BrukerId(I1)
        private val ID2 = BrukerId(I2)
        private val P1 = Person(ID1,I1, A1, KommuneTilknytning(Kommune("0301")))
        private val P2 = Person(ID2, I2, A2, KommuneTilknytning(Kommune("1111")))

        @ServiceConnection
       private val redis = RedisContainer("redis:6.2.2")
    }
}
