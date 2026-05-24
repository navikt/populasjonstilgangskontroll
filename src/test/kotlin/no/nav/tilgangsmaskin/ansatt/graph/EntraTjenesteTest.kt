package no.nav.tilgangsmaskin.ansatt.graph

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.ENTRA_CACHES
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GEO_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GEO_OG_GLOBALE_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjenesteTest.EntraTestConfig
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidBeanConfig
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.getOne
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.util.*

@RestClientTest(components = [EntraGrupperRestClientAdapter::class, EntraTjeneste::class, EntraGrupperConfig::class, EntraGruppeBeanConfig::class, EntraOidBeanConfig::class])
@Import(EntraTestConfig::class)
@ApplyExtension(SpringExtension::class)
class EntraTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableResilientMethods
    class EntraTestConfig : CacheTestConfig(GRAPH)

    @MockkBean
    @Suppress("unused")
    private lateinit var oidTjeneste: EntraOidTjeneste

    @MockkBean
    @Suppress("unused")
    private lateinit var entraOidClient: EntraOidClient

    @Autowired
    private lateinit var tjeneste: EntraTjeneste

    @Autowired
    private lateinit var server: MockRestServiceServer

    @Autowired
    private lateinit var cfg: EntraGrupperConfig

    @Autowired
    private lateinit var cache: CacheOperations


    init {

        beforeEach {
            server.reset()
            cache.clear(ENTRA_CACHES)
        }

        afterEach {
            server.verify()
        }

        Given("geoGrupper") {
            When("ansatt har geo-grupper") {
                Then("returnerer geo-grupper for ansatt") {
                    server.expect(requestTo(cfg.grupperURI("$OID", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1, GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoGrupper(ANSATTID, OID) shouldBe GEO_GRUPPER
                }
            }

            When("ansatt ikke har geo-grupper") {
                Then("returnerer tomt sett") {
                    server.expect(requestTo(cfg.grupperURI("$OID", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""{ "value": [] }""", APPLICATION_JSON))

                    tjeneste.geoGrupper(ANSATTID, OID).shouldBeEmpty()
                }
            }

            When("responsen inneholder @odata.nextLink") {
                Then("folger paginering via @odata.nextLink") {
                    val nextUrl = "http://graph/users/$OID/memberOf?page=2"
                    server.expect(requestTo(cfg.grupperURI(OID.toString(), false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""
                            {
                              "@odata.nextLink": "$nextUrl",
                              "value": [{ "id": "$GRUPPE1", "displayName": "0000-GA-GEO-$GRUPPE1" }]
                            }
                        """.trimIndent(), APPLICATION_JSON))
                    server.expect(requestTo(nextUrl))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoGrupper(ANSATTID, OID) shouldBe GEO_GRUPPER
                }
            }
        }

        Given("geoOgGlobaleGrupper") {
            When("ansatt har geo- og globale grupper") {
                Then("returnerer geo- og globale grupper for ansatt") {
                    server.expect(requestTo(cfg.grupperURI(OID.toString(), true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1, GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoOgGlobaleGrupper(ANSATTID, OID) shouldBe GEO_GRUPPER
                }
            }

            When("ansatt ikke har grupper") {
                Then("returnerer tom mengde") {
                    server.expect(requestTo(cfg.grupperURI(OID.toString(), true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""{ "value": [] }""", APPLICATION_JSON))

                    tjeneste.geoOgGlobaleGrupper(ANSATTID, OID).shouldBeEmpty()
                }
            }
        }

        Given("cache") {
            When("geoGrupper kalles to ganger for samme ansatt") {
                Then("server kalles kun én gang") {
                    server.expect(requestTo(cfg.grupperURI(OID.toString(), false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1), APPLICATION_JSON))

                    val first  = tjeneste.geoGrupper(ANSATTID, OID)
                    val second = tjeneste.geoGrupper(ANSATTID, OID)

                    first shouldBe second
                    cache.getOne<Set<EntraGruppe>>(GEO_CACHE, ANSATTID.verdi) shouldBe first
                }
            }

            When("geoOgGlobaleGrupper kalles to ganger for samme ansatt") {
                Then("server kalles kun én gang") {
                    server.expect(requestTo(cfg.grupperURI(OID.toString(), true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1), APPLICATION_JSON))

                    val first  = tjeneste.geoOgGlobaleGrupper(ANSATTID, OID)
                    val second = tjeneste.geoOgGlobaleGrupper(ANSATTID, OID)

                    first shouldBe second
                    cache.getOne<Set<EntraGruppe>>(GEO_OG_GLOBALE_CACHE, ANSATTID.verdi) shouldBe first
                }
            }

            When("to ulike ansatte slås opp") {
                Then("caches separat — server kalles to ganger") {
                    server.expect(requestTo(cfg.grupperURI("$OID", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1), APPLICATION_JSON))
                    server.expect(requestTo(cfg.grupperURI("$OID2", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoGrupper(ANSATTID, OID)  shouldBe setOf(GEO_GRUPPE1)
                    tjeneste.geoGrupper(ANSATTID2, OID2) shouldBe setOf(GEO_GRUPPE2)
                }
            }

            When("geoGrupper og geoOgGlobaleGrupper kalles for samme ansatt") {
                Then("caches separat — server kalles to ganger") {
                    server.expect(requestTo(cfg.grupperURI("$OID", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1), APPLICATION_JSON))
                    server.expect(requestTo(cfg.grupperURI("$OID", true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoGrupper(ANSATTID, OID)
                    tjeneste.geoOgGlobaleGrupper(ANSATTID, OID)
                }
            }
        }
    }

    private fun grupperRespons(vararg ids: UUID) = """
        {
          "value": [
            ${ids.joinToString(",") {
        """
                { "id": "$it", "displayName": "0000-GA-GEO-$it" }
                """.trimIndent() }}
          ]
        }
    """.trimIndent()

    private companion object {

        private val OID       = UUID.randomUUID()
        private val OID2     = UUID.randomUUID()
        private val ANSATTID  = AnsattId("Z999999")
        private val ANSATTID2 = AnsattId("Z888888")
        private val GRUPPE1  = UUID.randomUUID()
        private val GRUPPE2  = UUID.randomUUID()
        private val GEO_GRUPPE1 =  EntraGruppe(GRUPPE1, "0000-GA-GEO-$GRUPPE1")
        private val GEO_GRUPPE2 =  EntraGruppe(GRUPPE2, "0000-GA-GEO-$GRUPPE2")
        private val GEO_GRUPPER = setOf(GEO_GRUPPE1,GEO_GRUPPE2)
    }
}
