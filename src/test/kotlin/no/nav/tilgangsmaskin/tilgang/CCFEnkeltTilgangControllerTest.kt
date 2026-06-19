package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import io.mockk.justRun
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.post

class CCFEnkeltTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("CCF enkeltoppslag") {

            beforeEach { every { token.erCC } returns true }

            When("komplett kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("ccf-komplett")) }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isNoContent() } }
                        .andDo { handle(dokumenterMedAuth("ccf-kjerne")) }
                }
            }

            When("komplett kalles med OBO-token") {
                Then("returnerer 401") {
                    every { token.erCC } returns false
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 401") {
                    every { token.erCC } returns false
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("ccf/komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("ccf/komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("ccf/kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("ccf/kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("ccf/komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("ccf/kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }
    }
}
