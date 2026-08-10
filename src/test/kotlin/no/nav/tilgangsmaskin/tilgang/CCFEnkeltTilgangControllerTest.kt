package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import io.mockk.justRun
import no.nav.tilgangsmaskin.felles.rest.TokenType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post

class CCFEnkeltTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("CCF enkeltoppslag") {

            beforeEach { every { token.type } returns TokenType.CCF }

            When("komplett kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("$PROD_BASE_PATH/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }.andDo { handle(dokumenterMedAuth("ccf-komplett")) }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("$PROD_BASE_PATH/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"$brukerId\""
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }.andDo { handle(dokumenterMedAuth("ccf-kjerne")) }
                }
            }

            When("ccf/komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("ccf/komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("ccf/kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"\""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("ccf/kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("ccf/komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/komplett/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("ccf/kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/kjerne/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }
        }
    }
}
