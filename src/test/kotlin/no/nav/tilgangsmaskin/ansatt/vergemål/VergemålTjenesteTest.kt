package no.nav.tilgangsmaskin.ansatt.vergemål

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.Companion.VERGEMÅL_PATH
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL_BASE
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder.fromUriString

@RestClientTest(components = [VergemålRestClientAdapter::class, VergemålBeanConfig::class, VergemålTjeneste::class, DefaultRestErrorHandler::class])
@EnableConfigurationProperties(VergemålConfig::class)
@ApplyExtension(SpringExtension::class)
class VergemålTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableResilientMethods
    class Config

    @MockkBean
    private lateinit var nom: NomTjeneste

    @Autowired
    lateinit var tjeneste: VergemålTjeneste

    @Autowired
    lateinit var server: MockRestServiceServer

    init {
        afterEach { server.verify() }

        Given("oppslag av vergemål for ansatt") {
            beforeEach { every { nom.fnrForAnsatt(ANSATT_ID) } returns IDENT }

            When("ansatt har vergemål") {
                Then("returnerer brukerId-er for vergehavere") {
                    server.expect(requestTo(VERGEMÅL_URI))
                        .andExpect(method(POST))
                        .andRespond(withSuccess("""
                            [
                              {
                                "vergehaver": "${BRUKER1.verdi}",
                                "verge": "${IDENT.verdi}",
                                "leserettigheter": ["DAG"],
                                "skriverettigheter": []
                              },
                              {
                                "vergehaver": "${BRUKER2.verdi}",
                                "verge": "${IDENT.verdi}",
                                "leserettigheter": ["PEN"],
                                "skriverettigheter": ["PEN"]
                              }
                            ]
                        """.trimIndent(), APPLICATION_JSON))

                    tjeneste.vergemål(ANSATT_ID) shouldBe setOf(BRUKER1, BRUKER2)
                }
            }

            When("ansatt har ingen vergemål") {
                Then("returnerer tom liste") {
                    server.expect(requestTo(VERGEMÅL_URI))
                        .andExpect(method(POST))
                        .andRespond(withSuccess("[]", APPLICATION_JSON))

                    tjeneste.vergemål(ANSATT_ID) shouldBe emptySet()
                }
            }
        }

        Given("ansatt ikke funnet i NOM") {
            When("nom returnerer null") {
                Then("returnerer tom liste uten HTTP-kall") {
                    every { nom.fnrForAnsatt(ANSATT_ID) } returns null

                    tjeneste.vergemål(ANSATT_ID) shouldBe emptySet()
                }
            }
        }

        Given("feilhaandtering") {
            beforeEach { every { nom.fnrForAnsatt(ANSATT_ID) } returns IDENT }

            When("tjenesten returnerer 404") {
                Then("kaster NotFoundRestException uten retry") {
                    server.expect(requestTo(VERGEMÅL_URI))
                        .andExpect(method(POST))
                        .andRespond(withStatus(NOT_FOUND))

                    shouldThrow<NotFoundRestException> { tjeneste.vergemål(ANSATT_ID) }
                }
            }

            When("tjenesten returnerer 401") {
                Then("kaster IrrecoverableRestException uten retry") {
                    server.expect(requestTo(VERGEMÅL_URI))
                        .andExpect(method(POST))
                        .andRespond(withStatus(UNAUTHORIZED))

                    shouldThrow<IrrecoverableRestException> { tjeneste.vergemål(ANSATT_ID) }
                }
            }

            When("tjenesten returnerer 500") {
                Then("kaster RecoverableRestException etter 4 forsøk") {
                    server.expect(times(4), requestTo(VERGEMÅL_URI))
                        .andExpect(method(POST))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    shouldThrow<RecoverableRestException> { tjeneste.vergemål(ANSATT_ID) }
                }
            }
        }
    }

    companion object {
        private val ANSATT_ID = AnsattId("Z999999")
        private val IDENT = BrukerId("08526835670")
        private val BRUKER1 = BrukerId("20478606614")
        private val BRUKER2 = BrukerId("03508331575")
        private val VERGEMÅL_URI = fromUriString("$VERGEMÅL_BASE$VERGEMÅL_PATH").build().toUri()
    }
}
