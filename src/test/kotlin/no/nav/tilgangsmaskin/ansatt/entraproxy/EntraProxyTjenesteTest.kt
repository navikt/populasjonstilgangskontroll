package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ANSATT_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyClient.Companion.ENHETER_PATH
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.PROXY_BASE
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(components = [EntraProxyRestClientAdapter::class, EntraProxyBeanConfig::class, EntraProxyTjeneste::class, DefaultRestErrorHandler::class])
@EnableConfigurationProperties(EntraProxyConfig::class)
@ApplyExtension(SpringExtension::class)
class EntraProxyTjenesteTest : BehaviorSpec() {

    @Autowired
    lateinit var tjeneste: EntraProxyTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer



    init {
        afterEach { server.verify() }

        Given("oppslag av enhet for ansatt") {
            When("ansatt eksisterer") {
                Then("returnerer enhet for ansatt") {
                    server.expect(requestTo(ansattUrl()))
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
        }

        Given("oppslag av enheter for ansatt") {
            When("ansatt er tilknyttet enheter") {
                Then("returnerer liste av enheter for ansatt") {
                    server.expect(requestTo(enheterUrl()))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""
                            [
                              { "enhetnummer": "1234", "navn": "NAV Testkontor" },
                              { "enhetnummer": "5678", "navn": "NAV Annenkontor" }
                            ]
                        """.trimIndent(), APPLICATION_JSON))

                    tjeneste.enheter(ansattId) shouldBe setOf(
                        Enhet(Enhetsnummer("1234"), "NAV Testkontor"),
                        Enhet(Enhetsnummer("5678"), "NAV Annenkontor"))
                }
            }

            When("ansatt ikke er tilknyttet enheter") {
                Then("returneres tom liste") {
                    server.expect(requestTo(enheterUrl()))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("[]", APPLICATION_JSON))

                    tjeneste.enheter(ansattId) shouldBe emptySet()
                }
            }
        }

        Given("feilhaandtering") {
            When("tjenesten returnerer 404") {
                Then("kaster NotFoundRestException") {
                    server.expect(requestTo(ansattUrl()))
                        .andExpect(method(GET))
                        .andRespond(withStatus(NOT_FOUND))

                    shouldThrow<NotFoundRestException> {
                        tjeneste.enhet(ansattId)
                    }
                }
            }

            When("tjenesten returnerer 401") {
                Then("kaster IrrecoverableRestException") {
                    server.expect(requestTo(ansattUrl()))
                        .andExpect(method(GET))
                        .andRespond(withStatus(UNAUTHORIZED))

                    shouldThrow<IrrecoverableRestException> {
                        tjeneste.enhet(ansattId)
                    }
                }
            }

            When("tjenesten returnerer 500") {
                Then("kaster RecoverableRestException") {
                    server.expect(times(4), requestTo(ansattUrl()))
                        .andExpect(method(GET))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    shouldThrow<RecoverableRestException> {
                        tjeneste.enhet(ansattId)
                    }
                }
            }

            When("tjenesten returnerer 503") {
                Then("kaster RecoverableRestException") {
                    server.expect(times(4), requestTo(enheterUrl()))
                        .andExpect(method(GET))
                        .andRespond(withStatus(SERVICE_UNAVAILABLE))

                    shouldThrow<RecoverableRestException> {
                        tjeneste.enheter(ansattId)
                    }
                }
            }
        }
    }

    private val ansattId = AnsattId("Z999999")
    private fun ansattUrl() = "$PROXY_BASE${ANSATT_PATH.replace("{navIdent}", ansattId.verdi)}"
    private fun enheterUrl() = "$PROXY_BASE${ENHETER_PATH.replace("{navIdent}", ansattId.verdi)}"
}
