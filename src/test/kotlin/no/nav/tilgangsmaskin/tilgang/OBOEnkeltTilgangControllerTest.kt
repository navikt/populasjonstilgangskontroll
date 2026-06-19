package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import io.mockk.justRun
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.motor.KjerneRegel
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post

class OBOEnkeltTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO enkeltoppslag") {

            beforeEach { every { token.erObo } returns true }

            When("komplett kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("obo-komplett")) }
                }
            }

            When("komplett avviser tilgang") {
                Then("returnerer 403 med komplett ProblemDetail") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(brukerId)).build()
                    val testRegel = object : KjerneRegel {
                        override val metadata = RegelMetadata(STRENGT_FORTROLIG)
                        override fun evaluer(ansatt: Ansatt, bruker: Bruker) = false
                    }
                    every { regelTjeneste.kompletteRegler(ansattId, brukerId) } throws
                        RegelException(testAnsatt, testBruker, testRegel)
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect {
                        status { isForbidden() }
                        jsonPath("$.title") { value("AVVIST_STRENGT_FORTROLIG_ADRESSE") }
                        jsonPath("$.status") { value(403) }
                        jsonPath("$.instance") { isString() }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                        jsonPath("$.kanOverstyres") { value(false) }
                    }.andDo { handle(dokumenterMedAuth("obo-komplett-avvist", problemDetailFields)) }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("$DEFAULT_PREFIX/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("obo-kjerne")) }
                }
            }

            When("komplett kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erObo } returns false
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erObo } returns false
                    mockMvc.post("$DEFAULT_PREFIX/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/komplett") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/kjerne") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }

    }
}
