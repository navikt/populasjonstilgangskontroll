package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(components = [EntraProxyRestClientAdapter::class, EntraProxyBeanConfig::class, EntraProxyTjeneste::class])
@EnableConfigurationProperties(EntraProxyConfig::class)
@TestPropertySource(properties = ["entra-proxy.base-uri=http://localhost"])
@ApplyExtension(SpringExtension::class)
class EntraProxyTjenesteTest : DescribeSpec() {

    @Autowired lateinit var tjeneste: EntraProxyTjeneste
    @Autowired lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: EntraProxyConfig

    private val ansattId = AnsattId("Z999999")

    init {
        afterEach { server.verify() }

        describe("enhet") {
            it("returnerer enhet for ansatt") {
                server.expect(requestTo(cfg.brukerURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess("""
                        {
                          "navIdent": "Z999999",
                          "enhet": {
                            "enhetnummer": "1234",
                            "navn": "NAV Testkontor"
                          }
                        }
                    """.trimIndent(), APPLICATION_JSON))

                tjeneste.enhet(ansattId) shouldBe Enhet(Enhetsnummer("1234"), "NAV Testkontor")
            }
        }

        describe("enheter") {

            it("returnerer liste av enheter for ansatt") {
                server.expect(requestTo(cfg.enheterURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess("""
                        [
                          { "enhetnummer": "1234", "navn": "NAV Testkontor" },
                          { "enhetnummer": "5678", "navn": "NAV Annenkontor" }
                        ]
                    """.trimIndent(), APPLICATION_JSON))

                tjeneste.enheter(ansattId) shouldBe setOf(
                    Enhet(Enhetsnummer("1234"), "NAV Testkontor"),
                    Enhet(Enhetsnummer("5678"), "NAV Annenkontor")
                )
            }

            it("returnerer tom liste når ansatt ikke har enheter") {
                server.expect(requestTo(cfg.enheterURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess("[]", APPLICATION_JSON))

                tjeneste.enheter(ansattId) shouldBe emptySet()
            }
        }
    }
}
