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
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount.never
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import java.time.Duration.ofSeconds

@RestClientTest(components = [PdlTjeneste::class, PdlClient::class,PdlConfig::class])
@EnableConfigurationProperties(PdlConfig::class)
@Import(PdlTestConfig::class)
@TestPropertySource(properties = [
    "pdl.base-uri=http://pdl",
])
@EnableResilientMethods
@ApplyExtension(SpringExtension::class)
class PdlTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching
    class PdlTestConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(PDL)
        @Bean fun cache(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)

        @Bean
        fun pdlClient(b: RestClient.Builder, cfg: PdlConfig) =
            createClient<PdlClient>(cfg, b)
    }

    @MockkBean lateinit var graphQL: PdlSyncGraphQLClientAdapter

    @Autowired lateinit var pdl: PdlTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: PdlConfig
    @Autowired lateinit var cacheManager: CacheManager
    @Autowired lateinit var cache: CacheOperations
    @Autowired lateinit var mapper: JsonMapper

    init {
        beforeEach {
            server.reset()
            cacheManager.getCache(PDL)?.clear()
            every { graphQL.partnere(any()) } returns emptySet()
        }
        afterEach { server.verify() }

        Given("config") {
            When("navn og caches sjekkes") {
                Then("navn er korrekt og antall caches er 2") {
                    cfg.navn shouldBe PDL
                    cfg.caches.size shouldBe 2
                }
            }
        }

        Given("medFamilie") {
            When("person ikke er i cache") {
                Then("kalles REST og cache oppdateres") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medFamilie(I1) shouldBe P1
                    cache.getOne(PDL_MED_FAMILIE_CACHE, I1, Person::class) shouldBe P1
                }
            }
        }

        Given("medUtvidetFamilie") {
            When("person ikke er i cache") {
                Then("kalles REST og cache oppdateres") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1) shouldBe P1
                    cache.getOne(PDL_MED_UTVIDET_FAMILIE_CACHE, I1, Person::class) shouldBe P1
                }
            }

            When("person allerede er i cache") {
                Then("kalles ikke REST på nytt") {
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1)
                    server.verify()
                    server.reset()

                    server.expect(never(), requestTo(cfg.personURI))
                    pdl.medUtvidetFamilie(I1) shouldBe P1
                }
            }

            When("GraphQL returnerer en partner") {
                Then("inkluderes partneren i familie") {
                    val partner = FamilieMedlem(BrukerId("12345678901"), PARTNER)
                    every { graphQL.partnere(I1) } returns setOf(partner)
                    server.expect(requestTo(cfg.personURI))
                        .andRespond(withSuccess(mapper.writeValueAsString(pdlRespons(P1)), APPLICATION_JSON))
                    pdl.medUtvidetFamilie(I1).familie.partnere shouldBe setOf(partner)
                }
            }
        }

        Given("personer") {
            When("én person er i cache og én er ikke") {
                Then("kalles REST kun for cache-misser") {
                    cache.putOne(PDL_MED_FAMILIE_CACHE, I1, P1, ofSeconds(2))
                    server.expect(requestTo(cfg.personerURI))
                        .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder listOf(P1, P2)
                }
            }

            When("alle er i cache") {
                Then("kalles ikke REST") {
                    val entries = mapOf(I1 to P1, I2 to P2)
                    cache.putMany(PDL_MED_FAMILIE_CACHE, entries, ofSeconds(2))
                    server.expect(never(), requestTo(cfg.personerURI))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder entries.values
                }
            }

            When("ett element slettes fra cache") {
                Then("kalles REST for det slettede elementet og cache oppdateres") {
                    val innslag = mapOf(I1 to P1, I2 to P2)
                    cache.putMany(PDL_MED_FAMILIE_CACHE, innslag, ofSeconds(200))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder innslag.values
                    cache.delete(PDL_MED_FAMILIE_CACHE, I2)
                    server.expect(requestTo(cfg.personerURI))
                        .andRespond(withSuccess(restRespons(mapper, P2), APPLICATION_JSON))
                    pdl.personer(IDS) shouldContainExactlyInAnyOrder innslag.values
                    cache.getOne(PDL_MED_FAMILIE_CACHE, I2, Person::class) shouldBe P2
                }
            }

            When("settet er tomt") {
                Then("kalles ikke REST") {
                    server.expect(never(), requestTo(cfg.personerURI))
                    pdl.personer(emptySet()) shouldContainExactlyInAnyOrder emptyList()
                }
            }
        }
    }

    companion object {
        const val I1 = "03508331575"
        const val I2 = "20478606614"
        val IDS = setOf(I1, I2)
        val P1 = tilPerson(BrukerBuilder(BrukerId(I1))
            .aktørId(AktørId("1234567890123"))
            .gt(KommuneTilknytning(Kommune("0301")))
            .build())
        val P2 = tilPerson(BrukerBuilder(BrukerId(I2))
            .aktørId(AktørId("9876543210987"))
            .gt(UtenlandskTilknytning())
            .build())
    }
}