package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import io.mockk.justRun
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
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
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(document("obo-komplett")) }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(document("obo-kjerne")) }
                }
            }

            When("komplett kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/komplett") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = TEXT_PLAIN; content = ""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = TEXT_PLAIN; content = "   "
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN; content = ""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN; content = "   "
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = TEXT_PLAIN
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }

        Given("Overstyr") {

            val gyldigTil = LocalDate.now().plusMonths(2)

            beforeEach { every { token.erObo } returns true }

            When("enkelttilgang kalles med gyldig request og OBO-token") {
                Then("returnerer 202") {
                    every { enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattId, any(), any()) } returns true
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isAccepted() } }
                        .andDo { handle(document("obo-overstyr")) }
                }
            }

            When("enkelttilgang kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.erCC } returns true
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("begrunnelse er for kort") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"For kort","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isBadRequest() } }
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
                    }.andExpect { status { isBadRequest() } }
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
