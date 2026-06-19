package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import io.mockk.justRun
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.motor.OverstyrbarRegel
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.post
import java.time.LocalDate

class OBOEnkeltTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO enkeltoppslag") {

            beforeEach { every { token.erObo } returns true }

            When("komplett kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("obo-komplett")) }
                }
            }

            When("komplett avviser tilgang") {
                Then("returnerer 403 med komplett ProblemDetail") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(brukerId)).build()
                    val testRegel = object : OverstyrbarRegel {
                        override val metadata = RegelMetadata(STRENGT_FORTROLIG)
                        override fun evaluer(ansatt: Ansatt, bruker: Bruker) = false
                    }
                    every { regelTjeneste.kompletteRegler(ansattId, brukerId) } throws
                        RegelException(testAnsatt, testBruker, testRegel)
                    mockMvc.post("/api/v1/komplett") {
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
                        jsonPath("$.kanOverstyres") { value(true) }
                    }.andDo { handle(dokumenterMedAuth("obo-komplett-avvist", problemDetailFields)) }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("obo-kjerne")) }
                }
            }

            When("komplett kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }

        Given("Enkelttilgang") {

            val gyldigTil = LocalDate.now().plusMonths(2)

            beforeEach { every { token.erObo } returns true }

            When("enkelttilgang kalles med gyldig request og OBO-token") {
                Then("returnerer 202 og dokumenteres i rest docs") {
                    every { enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattId, any(), any()) } returns true
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isAccepted() } }
                        .andDo { handle(dokumenterMedAuth("obo-enkelttilgang")) }
                }
            }

            When("enkelttilgang kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erCC } returns true
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("enkelttilgang kalles uten token") {
                Then("returnerer 401") {
                    every { token.erCC } returns false
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect {
                        status { isUnauthorized() }
                        jsonPath("$.title") { value("Unauthorized") }
                        jsonPath("$.status") { value(401) }
                        jsonPath("$.instance") { value("/api/v1/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(document("obo-enkelttilgang-uten-token", problemDetailFields)) }
                }
            }

            When("begrunnelse er for kort") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"For kort","gyldigtil":"$gyldigTil"}"""
                    }.andExpect {
                        status { isBadRequest() }
                        jsonPath("$.title") { value("Bad Request") }
                        jsonPath("$.status") { value(400) }
                        jsonPath("$.instance") { value("/api/v1/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(dokumenterMedAuth("obo-enkelttilgang-begrunnelse-for-kort", problemDetailFields)) }
                }
            }

            When("begrunnelse er for lang") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"${"x".repeat(401)}","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("gyldigtil er i fortiden") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"${LocalDate.now().minusDays(1)}"}"""
                    }.andExpect {
                        status { isBadRequest() }
                        jsonPath("$.title") { value("Bad Request") }
                        jsonPath("$.status") { value(400) }
                        jsonPath("$.instance") { value("/api/v1/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(dokumenterMedAuth("obo-enkelttilgang-validering-gyldigtil", problemDetailFields)) }
                }
            }

            When("gyldigtil er mer enn 3 måneder frem i tid") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"${LocalDate.now().plusMonths(4)}"}"""
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }
    }
}
