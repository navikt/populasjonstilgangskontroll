package no.nav.tilgangsmaskin.felles.cache

import io.mockk.mockk
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
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.SECONDS

class PersonerCacheTest : AbstractCacheTest() {

    @Autowired
    private lateinit var mapper: JsonMapper

    private lateinit var pdl: PdlTjeneste
    private lateinit var mockServer: MockRestServiceServer

    override fun cacheConfigurations() = mapOf(
        PDL_MED_FAMILIE_CACHE.name to defaultCacheConfig()
            .prefixCacheNameWith(PDL)
            .disableCachingNullValues()
    )

    @BeforeEach
    fun setUp() {
        val restClientBuilder = RestClient.builder().baseUrl("${cfg.baseUri}")
        mockServer = bindTo(restClientBuilder).build()
        pdl = PdlTjeneste(PdlRestClientAdapter(restClientBuilder.build(), cfg, mapper), mockk(), cache, cfg)
        IDS.forEach { cache.delete(PDL_MED_FAMILIE_CACHE, it) }
    }


    @Test
    @DisplayName("Rest kalles kun for cache-misser, treff hentes fra cache")
    fun restKallesKunForCacheMisser() {
        putOne(P1)
        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))

        assertThat(pdl.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        mockServer.verify()
    }

    @Test
    @DisplayName("Rest kalles ikke når alle er i cache, slett ett element og verifiser at rest kalles for det elementet og ikke det som fortsatt er i cache")
    fun restKallesIkkeNårAlleErICache() {
        putMany(P1, P2)
        assertThat(pdl.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        mockServer.verify()
        cache.delete(PDL_MED_FAMILIE_CACHE, I2)
        mockServer.expect(requestTo(cfg.personerURI))
            .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
        assertThat(pdl.personer(IDS)).containsExactlyInAnyOrder(P1, P2)
        assertThat(getMany(IDS).keys).containsExactlyInAnyOrderElementsOf(IDS)
        mockServer.verify()
    }

    @Test
    @DisplayName("Verifiser at lytteren publiserer en CacheInnslagFjernetEvent når en nøkkel utløper")
    fun listenerPublisererEventVedUtløp() {
        val mottatt = mutableListOf<String>()
        ctx.addApplicationListener(ApplicationListener<CacheInnslagFjernetEvent> { mottatt.add(it.nøkkel) })
        putOne(P1)
        await.atMost(3, SECONDS).until {
            mottatt.any { it.contains(P1.brukerId.verdi) }
        }
    }

    private fun putMany(vararg personer: Person, duration: Duration = ofSeconds(1)) =
        cache.putMany(PDL_MED_FAMILIE_CACHE, personer.associateBy { it.brukerId.verdi }, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(PDL_MED_FAMILIE_CACHE, ids, Person::class)

    private fun putOne(person: Person, duration: Duration = ofSeconds(2)) =
        cache.putOne(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi, person, duration)

    private fun getOne(id: String) =
        cache.getOne(PDL_MED_FAMILIE_CACHE, id, Person::class)

    companion object {
        private val A1 = AktørId("1234567890123")
        private val A2 = AktørId("9876543210987")
        private val P1 = Person(BrukerId(I1), I1, A1, KommuneTilknytning(Kommune("0301")))
        private val P2 = Person(BrukerId(I2), I2, A2, KommuneTilknytning(Kommune("1111")))
        private val cfg = PdlConfig(URI.create("http://pdl"))

        private fun restRespons(mapper: JsonMapper, p: Person) = mapper.writeValueAsString(
            mapOf(p.brukerId.verdi to PdlRespons(
                PdlPerson(),
                PdlIdenter(listOf(
                    PdlIdent(p.brukerId.verdi, false, FOLKEREGISTERIDENT),
                    PdlIdent(p.aktørId.verdi, false, AKTORID)
                )),
                PdlGeografiskTilknytning(KOMMUNE, GTKommune((p.geoTilknytning as KommuneTilknytning).kommune.verdi))
            ))
        )
    }
}
