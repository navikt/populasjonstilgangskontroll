
package no.nav.tilgangsmaskin.ansatt.vergemål

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.Companion.VERGEMÅL_PATH
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGE_CACHE
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import no.nav.tilgangsmaskin.felles.cache.getOne
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.felles.rest.PropertySettingTestContextInitializer
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.ExpectedCount.times
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.util.UriComponentsBuilder.fromUriString


import no.nav.tilgangsmaskin.felles.rest.OAuth2ClientTestConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.test.context.ContextConfiguration
import java.net.URI

@TestConfiguration
class VergemålTestConfig : CacheTestConfig(VERGEMÅL)

@RestClientTest
@EnableResilientMethods
@Import(VergemålTestConfig::class, OAuth2ClientTestConfig::class)
@ContextConfiguration(classes = [VergemålConfig::class, VergemålTjeneste::class], initializers = [PropertySettingTestContextInitializer::class])
class VergemålTjenesteTest : BehaviorSpec() {

    @MockkBean
    private lateinit var nom: NomTjeneste

    @Autowired
    lateinit var tjeneste: VergemålTjeneste

    @Autowired
    lateinit var cfg: VergemålConfig

    @Autowired
    lateinit var server: MockRestServiceServer

    @Autowired
    lateinit var cache: CacheOperations

    init {
        beforeEach {
            server.reset()
            cache.clear(VERGE_CACHE)
        }

        afterEach { server.verify() }

        Given("oppslag av vergemål for ansatt") {
            beforeEach { every { nom.fnrForAnsatt(ANSATT_ID) } returns IDENT }

            When("ansatt har vergemål") {
                Then("returnerer brukerId-er for vergehavere") {
                    server.expect(requestTo(uri(cfg.baseUri)))
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

                    tjeneste.alle(ANSATT_ID) shouldBe setOf(BRUKER1, BRUKER2)
                }
            }

            When("ansatt har ingen vergemål") {
                Then("returnerer tom liste") {
                    server.expect(requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withSuccess("[]", APPLICATION_JSON))

                    tjeneste.alle(ANSATT_ID).shouldBeEmpty()
                }
            }
        }

        Given("caching av vergemål") {
            beforeEach {
                every { nom.fnrForAnsatt(ANSATT_ID) } returns IDENT
                every { nom.fnrForAnsatt(ANSATT_ID_2) } returns IDENT
            }

            When("samme ansatt slås opp to ganger") {
                Then("REST kalles kun én gang og resultat finnes i cache") {
                    server.expect(once(), requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withSuccess(vergemålRespons(), APPLICATION_JSON))

                    val første = tjeneste.alle(ANSATT_ID)
                    val andre = tjeneste.alle(ANSATT_ID)

                    første shouldBe setOf(BRUKER1, BRUKER2)
                    andre shouldBe første
                    cache.getOne<Set<BrukerId>>(VERGE_CACHE, ANSATT_ID.verdi) shouldBe første
                }
            }

            When("to ulike ansatte slås opp") {
                Then("REST kalles to ganger selv om NOM-ident er lik") {
                    server.expect(times(2), requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withSuccess(vergemålRespons(), APPLICATION_JSON))

                    tjeneste.alle(ANSATT_ID) shouldBe setOf(BRUKER1, BRUKER2)
                    tjeneste.alle(ANSATT_ID_2) shouldBe setOf(BRUKER1, BRUKER2)
                }
            }
        }

        Given("ansatt ikke funnet i NOM") {
            When("nom returnerer null") {
                Then("returnerer tom liste uten HTTP-kall") {
                    every { nom.fnrForAnsatt(ANSATT_ID) } returns null

                    tjeneste.alle(ANSATT_ID).shouldBeEmpty()
                }
            }
        }

        Given("feilhåndtering") {
            beforeEach { every { nom.fnrForAnsatt(ANSATT_ID) } returns IDENT }

            When("tjenesten returnerer 404") {
                Then("kaster NotFoundRestException uten retry") {
                    server.expect(requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withStatus(NOT_FOUND))

                    shouldThrow<NotFoundRestException> { tjeneste.alle(ANSATT_ID) }
                }
            }

            When("tjenesten returnerer 401") {
                Then("kaster IrrecoverableRestException uten retry") {
                    server.expect(requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withStatus(UNAUTHORIZED))

                    shouldThrow<IrrecoverableRestException> { tjeneste.alle(ANSATT_ID) }
                }
            }

            When("tjenesten returnerer 500") {
                Then("kaster RecoverableRestException etter 4 forsøk") {
                    server.expect(times(4), requestTo(uri(cfg.baseUri)))
                        .andExpect(method(POST))
                        .andRespond(withStatus(INTERNAL_SERVER_ERROR))

                    shouldThrow<RecoverableRestException> { tjeneste.alle(ANSATT_ID) }
                }
            }
        }
    }

    companion object {
        private val ANSATT_ID = AnsattId("Z999999")
        private val ANSATT_ID_2 = AnsattId("Z888888")
        private val IDENT = BrukerId("08526835670")
        private val BRUKER1 = BrukerId("20478606614")
        private val BRUKER2 = BrukerId("03508331575")

        private fun uri(base: URI) = fromUriString("$base$VERGEMÅL_PATH").build().toUri()

        private fun vergemålRespons() = """
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
        """.trimIndent()
    }
}
