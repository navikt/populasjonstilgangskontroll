package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.rest.OAuth2ClientTestConfig
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.RestTjenesteTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess


@RestClientTest
@ContextConfiguration(classes = [EntraProxyTjeneste::class, EntraProxyConfig::class])
@Import(OAuth2ClientTestConfig::class)
@ConfigurationPropertiesScan
@EnableAutoConfiguration
class EntraProxyTjenesteTest : RestTjenesteTest() {

    @Autowired
    lateinit var tjeneste: EntraProxyTjeneste
    @Autowired
    lateinit var server: MockRestServiceServer
    @Autowired
    lateinit var cfg: EntraProxyConfig

    init {
        afterEach {
            server.verify()
        }

        Given("oppslag av enhet for ansatt") {
            When("ansatt eksisterer") {
                Then("returnerer enhet for ansatt") {
                    server.expect(requestTo(cfg.ansattUri(ANSATTID.verdi)))
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

                    tjeneste.enhet(ANSATTID) shouldBe Enhet(Enhetsnummer("1234"), "NAV Testkontor")
                }
            }
        }

        Given("oppslag av enheter for ansatt") {
            When("ansatt er tilknyttet enheter") {
                Then("returnerer liste av enheter for ansatt") {
                    server.expect(requestTo(cfg.enheterUri(ANSATTID.verdi)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""
                            [
                              { "enhetnummer": "1234", "navn": "NAV Testkontor" },
                              { "enhetnummer": "5678", "navn": "NAV Annenkontor" }
                            ]
                        """.trimIndent(), APPLICATION_JSON))

                    tjeneste.enheter(ANSATTID) shouldBe setOf(
                        Enhet(Enhetsnummer("1234"), "NAV Testkontor"),
                        Enhet(Enhetsnummer("5678"), "NAV Annenkontor"))
                }
            }

            When("ansatt ikke er tilknyttet enheter") {
                Then("returneres tom liste") {
                    server.expect(requestTo(cfg.enheterUri(ANSATTID.verdi)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("[]", APPLICATION_JSON))

                    tjeneste.enheter(ANSATTID).shouldBeEmpty()
                }
            }
        }

        Given("feilhaandtering") {
            When("tjenesten returnerer 404") {
                Then("kaster NotFoundRestException uten retry") {
                    server.expect(requestTo(cfg.ansattUri(ANSATTID.verdi)))
                        .andExpect(method(GET))
                        .andRespond(withStatus(NOT_FOUND))

                    shouldThrow<NotFoundRestException> {
                        tjeneste.enhet(ANSATTID)
                    }
                }
            }

            When("tjenesten returnerer 401") {
                Then("kaster IrrecoverableRestException uten retry") {
                    server.expect(requestTo(cfg.ansattUri(ANSATTID.verdi)))
                        .andExpect(method(GET))
                        .andRespond(withStatus(UNAUTHORIZED))

                    shouldThrow<IrrecoverableRestException> {
                        tjeneste.enhet(ANSATTID)
                    }
                }
            }

            When("tjenesten returnerer 500") {
                Then("kaster RecoverableRestException etter 4 forsøk") {
                    server.expect(times(4), requestTo(cfg.ansattUri(ANSATTID.verdi)))
                        .andExpect(method(GET))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    shouldThrow<RecoverableRestException> {
                        tjeneste.enhet(ANSATTID)
                    }
                }
            }
        }
    }


    companion object  {
        private val ANSATTID = AnsattId("Z999999")
    }
}
