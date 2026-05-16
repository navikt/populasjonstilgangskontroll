package no.nav.tilgangsmaskin.bruker.pdl

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.BrukerTilPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_UTVIDET_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlTestMapper.pdlRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlTestMapper.restRespons
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjenesteTest.PdlTestConfig
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient.Builder
import tools.jackson.databind.json.JsonMapper
import java.time.Duration.ofSeconds

@RestClientTest(components = [PdlConfig::class, PdlTjeneste::class])
@Import(PdlTestConfig::class)
@TestPropertySource(properties = ["PDL=pdl"])
@EnableResilientMethods
@ApplyExtension(SpringExtension::class)
class PdlTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    class PdlTestConfig : CacheTestConfig(PDL) {

        @Bean
        fun pdlClient(builder: Builder, cfg: PdlConfig) = createClient<PdlPipClient>(cfg, builder)
    }

    @MockkBean lateinit var graphQL: PdlSyncGraphQLClientAdapter

    @Autowired lateinit var pdl: PdlTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: PdlConfig
    @Autowired lateinit var cacheManager: CacheManager
    @Qualifier("cacheOperations") @Autowired lateinit var cache: CacheOperations
    @Autowired lateinit var mapper: JsonMapper

    init {
        beforeEach {
            server.reset()
            cacheManager.getCache(PDL)?.clear()
            every { graphQL.partnere(any()) } returns emptySet()
        }


        Given("medFamilie") {
            When("person ikke er cachet") {
                Then("kalles REST og cache oppdateres") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medFamilie(I1) shouldBe P1
                    server.verify()
                    cache.getOne(PDL_MED_FAMILIE_CACHE, I1, Person::class) shouldBe P1
                }
            }
        }

        Given("medUtvidetFamilie") {
            When("person ikke er cachet") {
                Then("kalles REST og cache oppdateres") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1) shouldBe P1
                    server.verify()
                    cache.getOne(PDL_MED_UTVIDET_FAMILIE_CACHE, I1, Person::class) shouldBe P1
                }
            }
            When("samme person slås opp to ganger") {
                Then("REST kalles kun én gang") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1)
                    server.verify()
                    server.reset()
                    server.expect(never(), requestTo(cfg.personURI))
                    pdl.medUtvidetFamilie(I1) shouldBe P1
                    server.verify()
                }
            }
            When("GraphQL returnerer partnere") {
                Then("inkluderes partnere i familien") {
                    val partner = FamilieMedlem(BrukerId("12345678901"), PARTNER)
                    every { graphQL.partnere(I1) } returns setOf(partner)
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1).familie.partnere shouldBe setOf(partner)
                    server.verify()
                }
            }
        }

        Given("personer") {
            When("én er i cache og én er cache-miss") {
                Then("REST kalles kun for cache-misser") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                    server.expect(requestTo(cfg.personerURI))
                        .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                    server.verify()
                }
            }
            When("alle er i cache") {
                Then("REST kalles ikke") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(2))
                    server.expect(never(), requestTo(cfg.personerURI))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                    server.verify()
                }
            }
            When("ett cache-innslag slettes") {
                Then("REST kalles for det slettede elementet") {
                    cache.putMany(PDL_MED_FAMILIE_CACHE, mapOf(I1 to P1, I2 to P2), ofSeconds(2))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                    cache.delete(PDL_MED_FAMILIE_CACHE, I2)
                    server.expect(requestTo(cfg.personerURI))
                        .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                    cache.getOne(PDL_MED_FAMILIE_CACHE, I2, Person::class) shouldBe P2
                    server.verify()
                }
            }
            When("settet er tomt") {
                Then("REST kalles ikke") {
                    server.expect(never(), requestTo(cfg.personerURI))
                    pdl.personer(emptySet()) shouldBe emptyList()
                    server.verify()
                }
            }
        }
    }

    companion object {
        const val I1 = "03508331575"
        const val I2 = "20478606614"

        val P1 = tilPerson(BrukerBuilder(BrukerId(I1))
            .aktørId(AktørId("1234567890123"))
            .gt(KommuneTilknytning(Kommune("0301")))
            .build())
        val P2 = tilPerson(BrukerBuilder(BrukerId(I2))
            .aktørId(AktørId("9876543210987"))
            .gt(UtenlandskTilknytning())
            .build())
        val IDS = setOf(P1, P2).mapTo(mutableSetOf()) { it.brukerId.verdi }
    }
}