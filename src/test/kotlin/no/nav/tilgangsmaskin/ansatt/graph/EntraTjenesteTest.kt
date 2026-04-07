package no.nav.tilgangsmaskin.ansatt.graph

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.Companion.setIDs
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.entries
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjenesteTest.CacheConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.util.*

@RestClientTest(components = [EntraRestClientAdapter::class, EntraClientBeanConfig::class, EntraTjeneste::class])
@EnableConfigurationProperties(EntraConfig::class)
@Import(CacheConfig::class)
@TestPropertySource(properties = ["graph.base-uri=http://graph"])
@ApplyExtension(SpringExtension::class)
class EntraTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching
    @EnableResilientMethods
    class CacheConfig {
        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager(GRAPH)
    }

    @MockkBean
    @Suppress("unused")
    private lateinit var oidTjeneste: AnsattOidTjeneste

    @Autowired
    private lateinit var tjeneste: EntraTjeneste

    @Autowired
    private lateinit var server: MockRestServiceServer

    @Autowired
    private lateinit var cfg: EntraConfig

    @Autowired
    private lateinit var cacheManager: CacheManager



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

    init {

        beforeEach {
            setIDs(entries.associate { it.property to UUID.randomUUID() })
            server.reset()
            cacheManager.getCache(GRAPH)?.clear()
        }

        afterEach {
            server.verify()
        }

        Given("geoGrupper") {
            When("ansatt har geo-grupper") {
                Then("returnerer geo-grupper for ansatt") {
                    server.expect(requestTo(cfg.grupperURI(oid.toString(), false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1, GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoGrupper(ansattId, oid) shouldBe setOf(
                        EntraGruppe(GRUPPE1, "0000-GA-GEO-$GRUPPE1"),
                        EntraGruppe(GRUPPE2, "0000-GA-GEO-$GRUPPE2")
                    )
                }
            }

            When("ansatt ikke har geo-grupper") {
                Then("returnerer tom mengde") {
                    server.expect(requestTo(cfg.grupperURI("$oid", false)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""{ "value": [] }""", APPLICATION_JSON))

                    tjeneste.geoGrupper(ansattId, oid) shouldBe emptySet()
                }
            }

            When("responsen inneholder @odata.nextLink") {
                Then("folger paginering via @odata.nextLink") {
                    val nextUrl = "http://graph/users/$oid/memberOf?page=2"
                    server.expect(requestTo(cfg.grupperURI(oid.toString(), false)))
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

                    tjeneste.geoGrupper(ansattId, oid) shouldBe setOf(
                        EntraGruppe(GRUPPE1, "0000-GA-GEO-$GRUPPE1"),
                        EntraGruppe(GRUPPE2, "0000-GA-GEO-$GRUPPE2")
                    )
                }
            }
        }

        Given("geoOgGlobaleGrupper") {
            When("ansatt har geo- og globale grupper") {
                Then("returnerer geo- og globale grupper for ansatt") {
                    server.expect(requestTo(cfg.grupperURI(oid.toString(), true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(grupperRespons(GRUPPE1, GRUPPE2), APPLICATION_JSON))

                    tjeneste.geoOgGlobaleGrupper(ansattId, oid) shouldBe setOf(
                        EntraGruppe(GRUPPE1, "0000-GA-GEO-$GRUPPE1"),
                        EntraGruppe(GRUPPE2, "0000-GA-GEO-$GRUPPE2")
                    )
                }
            }

            When("ansatt ikke har grupper") {
                Then("returnerer tom mengde") {
                    server.expect(requestTo(cfg.grupperURI(oid.toString(), true)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""{ "value": [] }""", APPLICATION_JSON))

                    tjeneste.geoOgGlobaleGrupper(ansattId, oid) shouldBe emptySet()
                }
            }
        }
    }

    private companion object {
        private val oid = UUID.fromString("11111111-1111-1111-1111-111111111111")
        private val ansattId = AnsattId("Z999999")
        private val GRUPPE1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        private val GRUPPE2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
    }
}
