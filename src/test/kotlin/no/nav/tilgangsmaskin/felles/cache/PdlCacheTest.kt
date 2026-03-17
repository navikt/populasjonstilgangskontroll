package no.nav.tilgangsmaskin.felles.cache

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import org.awaitility.kotlin.await
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.MockRestServiceServer.bindTo
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.*

class PdlCacheTest : AbstractCacheTest() {

    @Autowired
    private lateinit var mapper: JsonMapper
    private lateinit var pdl: PdlTjeneste
    private lateinit var mockServer: MockRestServiceServer

    override fun cacheConfigurations() = mapOf(
        PDL_MED_FAMILIE_CACHE.name to defaultCacheConfig()
            .prefixCacheNameWith(PDL)
            .disableCachingNullValues()
    )

    init {
        beforeEach {
            setUpCache()
            val restClientBuilder = RestClient.builder().baseUrl("${cfg.baseUri}")
            mockServer = bindTo(restClientBuilder).build()
            pdl = PdlTjeneste(PdlRestClientAdapter(restClientBuilder.build(), cfg, mapper), mockk(), cache, cfg)
            IDS.forEach { cache.delete(PDL_MED_FAMILIE_CACHE, it) }
        }

        describe("personer") {

            it("Rest kalles kun for cache-misser, treff hentes fra cache") {
                putOne(P1)
                mockServer.expect(requestTo(cfg.personerURI))
                    .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))

                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                mockServer.verify()
            }

            it("Rest kalles ikke når alle er i cache, slett ett element og verifiser at rest kalles for det elementet") {
                putMany(P1, P2)
                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                mockServer.verify()
                cache.delete(PDL_MED_FAMILIE_CACHE, I2)
                mockServer.expect(requestTo(cfg.personerURI))
                    .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                getMany(IDS).keys shouldContainExactlyInAnyOrder IDS
                mockServer.verify()
            }

            it("Rest kalles ikke når settet er tomt") {
                pdl.personer(emptySet()) shouldContainExactlyInAnyOrder emptyList()
                mockServer.verify()
            }

            it("Lytteren publiserer en CacheInnslagFjernetEvent når en nøkkel utløper") {
                val mottatt = mutableListOf<String>()
                ctx.addApplicationListener(ApplicationListener<CacheInnslagFjernetEvent> { mottatt.add(it.nøkkel) })
                putOne(P1)
                await.atMost(3, SECONDS).until {
                    mottatt.any { it.contains(P1.brukerId.verdi) }
                }
            }
        }
    }

    private fun putMany(vararg personer: Person, duration: Duration = ofSeconds(1)) =
        cache.putMany(PDL_MED_FAMILIE_CACHE, personer.associateBy { it.brukerId.verdi }, duration)

    private fun getMany(ids: Set<String>) =
        cache.getMany(PDL_MED_FAMILIE_CACHE, ids, Person::class)

    private fun putOne(person: Person, duration: Duration = ofSeconds(2)) =
        cache.putOne(PDL_MED_FAMILIE_CACHE, person.brukerId.verdi, person, duration)


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
