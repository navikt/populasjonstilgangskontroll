package no.nav.tilgangsmaskin.felles.cache

import com.ninjasquad.springmockk.MockkBean
import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import io.lettuce.core.RedisClient
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
import io.mockk.mockk
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
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
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager.builder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
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

    @Autowired
    private lateinit var ctx: ConfigurableApplicationContext

    private lateinit var listener: CacheElementUtløptLytter

    @Autowired
    private lateinit var cf: RedisConnectionFactory
    private lateinit var cache: CacheOperations
    private lateinit var tjeneste: PdlTjeneste
    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        every { token.system } returns "test"
        every { token.clusterAndSystem } returns "test:dev-gcp"

        val mgr = builder(cf)
            .withInitialCacheConfigurations(mapOf(
                PDL_MED_FAMILIE_CACHE.name to defaultCacheConfig()
                    .prefixCacheNameWith(PDL)
                    .disableCachingNullValues()
            ))
            .build().apply {
                getCache(PDL_MED_FAMILIE_CACHE.name) // init
            }
        val redisClient = RedisClient.create("redis://${redis.host}:${redis.firstMappedPort}")
        val handler = CacheNøkkelHandler(mgr.cacheConfigurations)

        cache = CacheClient(
            redisClient, handler, BulkCacheSuksessTeller(meterRegistry, token),
            BulkCacheTeller(meterRegistry, token), CacheConfig("user","pw",redis.host, redis.firstMappedPort.toString(),
                ofSeconds(1),
                ofSeconds(1))
        )

        listener = CacheElementUtløptLytter(redisClient, eventPublisher)

        val restClientBuilder = RestClient.builder().baseUrl("${URI.create("http://pdl")}")
        mockServer = bindTo(restClientBuilder).build().apply {
            reset()
        }

        val adapter = PdlRestClientAdapter(restClientBuilder.build(), cfg, mapper)
         tjeneste = PdlTjeneste(adapter, mockk(relaxed = true), cache, cfg).also { t ->
            PdlTjeneste::class.java.getDeclaredField("self").apply {
                isAccessible = true
                set(t, t)
            }
        }

    }



    @Test
    @DisplayName("Put og get en verdi, og verifiser at den er borte etter utløp")
    fun putAndGetOne() {
        putOne( P1)
        assertThat(getOne(I1)).isEqualTo(P1)
        await.atMost(2, SECONDS).until {
            getOne(I1) == null
        }
    }
    @Test
    @DisplayName("Put og get flere verdier, og verifiser at de er borte etter utløp")
    fun putAndGetMany() {
        putMany(P1, P2)
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        await.atMost(2, SECONDS).until {
            getMany(IDS).isEmpty()
        }
    }

    @Test
    @DisplayName("Rest kalles kun for cache-misser, treff hentes fra cache")
    fun restKallesKunForCacheMisser() {
        putOne(P1)
        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons(mapper,P2), APPLICATION_JSON))

        assertThat(tjeneste.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        mockServer.verify()
    }

    @Test
    @DisplayName("Rest kalles ikke når alle er i cache")
    fun restKallesIkkeNårAlleErICache() {
        putMany(P1, P2)
        assertThat(tjeneste.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        mockServer.verify()
        cache.delete(PDL_MED_FAMILIE_CACHE, I2)
        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons(mapper,P2), APPLICATION_JSON))
        assertThat(tjeneste.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        mockServer.verify()
    }
    

    @Test
    @DisplayName("Verifiser at lytteren publiserer en CacheInnslagFjernetEvent når en nøkkel utløper")
    fun listenerPublisererEventVedUtløp() {
        val mottattNøkler = mutableListOf<String>()
        ctx.addApplicationListener(
            ApplicationListener<CacheElementUtløptLytter.CacheInnslagFjernetEvent> { event ->
                mottattNøkler.add(event.nøkkel)
            }
        )

        putOne( P1)
        await.atMost(5, SECONDS).until {
            mottattNøkler.any { it.contains(P1.brukerId.verdi) }
        }
    }
    private fun putMany(vararg personer: Person, duration: Duration = ofSeconds(1)) =
        cache.putMany(PDL_MED_FAMILIE_CACHE, personer.associateBy { it.brukerId.verdi }, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(PDL_MED_FAMILIE_CACHE, ids, Person::class)

    private fun putOne(person: Person, duration: Duration = ofSeconds(2)) =
        cache.putOne(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi, person, duration)

    private fun getOne(id: String ) =
        cache.getOne(PDL_MED_FAMILIE_CACHE,id,  Person::class)

    companion object {
        private val A1 = AktørId("1234567890123")
        private val A2 = AktørId("9876543210987")
        private const val I1 = "03508331575"
        private const val I2 = "20478606614"
        private val IDS = setOf(I1, I2)
        private val ID1 = BrukerId(I1)
        private val ID2 = BrukerId(I2)
        private val P1 = Person(ID1,I1, A1, KommuneTilknytning(Kommune("0301")))
        private val P2 = Person(ID2, I2, A2, KommuneTilknytning(Kommune("1111")))

        private fun restRespons(mapper: JsonMapper, p: Person) = mapper.writeValueAsString(mapOf(p.brukerId.verdi to PdlRespons(
            PdlPerson(),
            PdlIdenter(listOf(
                PdlIdent(p.brukerId.verdi, false, FOLKEREGISTERIDENT),
                PdlIdent(p.aktørId.verdi, false, AKTORID)
            )),
            PdlGeografiskTilknytning(KOMMUNE, GTKommune((p.geoTilknytning as KommuneTilknytning).kommune.verdi))
        )))
        @ServiceConnection
        private val redis = RedisContainer(DEFAULT_IMAGE_NAME)

        private val cfg = PdlConfig(URI.create("http://pdl"))
    }
}
