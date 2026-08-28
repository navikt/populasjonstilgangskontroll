package no.nav.tilgangsmaskin.tilgang

import io.mockk.justRun
import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.rest.assertProblemDetailBody
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_STRENGT_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.KjerneRegel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post
import java.time.LocalDate

class OBOEnkeltTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO enkeltoppslag") {
            When("komplett kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun {
                        regelTjeneste.kompletteRegler(ansattId, brukerId)
                    }
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"$brukerId\""
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }.andDo {
                        handle(dokumenterMedAuth("obo-komplett"))
                    }
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
                    every {
                        regelTjeneste.kompletteRegler(ansattId, brukerId)
                    } throws RegelException(testAnsatt, testBruker, testRegel)

                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"$brukerId\""
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                    }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(any(), brukerId) }
                    mockMvc.post("$PROD_BASE_PATH/kjerne") {
                        contentType = APPLICATION_JSON
                        content = "\"$brukerId\""
                    }.andExpect {
                        status { isNoContent()
                        }
                    }.andDo { handle(dokumenterMedAuth("obo-kjerne")) }
                }
            }

            When("komplett kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"\""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("komplett kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"   \""
                    }.andExpect {
                        status { isBadRequest()
                        }
                    }
                }
            }

            When("kjerne kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/kjerne") {
                        contentType = APPLICATION_JSON
                        content = "\"\""
                    }.andExpect {
                        status { isBadRequest()
                        }
                    }
                }
            }

            When("kjerne kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/kjerne") {
                        contentType = APPLICATION_JSON; content = "\"   \""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("komplett kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                    }.andExpect {
                        status { isBadRequest()
                        }
                    }
                }
            }

            When("kjerne kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/kjerne") {
                        contentType = APPLICATION_JSON
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }
        }

        Given("Enkelttilgang") {

            val gyldigTil = LocalDate.now().plusMonths(2)
            val request = EnkeltTilgangData(BrukerId(brukerId), "En god begrunnelse", gyldigTil)

            When("enkelttilgang kalles med gyldig request og OBO-token") {
                Then("returnerer 202 og dokumenteres i rest docs") {
                    every { enkeltTilgangTjeneste.registrerTilgang(ansattId, request) } returns true
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(request)
                    }.andExpect {
                        status {
                            isAccepted()
                        }
                    }.andDo { handle(dokumenterMedAuth("obo-enkelttilgang")) }
                }
            }

            When("begrunnelse er for kort") {
                Then("returnerer 400 med valideringsfeil på begrunnelse") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(EnkeltTilgangData(BrukerId(brukerId), "For kort", gyldigTil))
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }.andReturn().assertProblemDetailBody(
                        mapper,
                        BAD_REQUEST,
                        "En eller flere felter er ugyldige",
                        title = "Validering feilet",
                        fields = listOf("begrunnelse"),
                        requireBody = false
                    )
                }
            }

            When("begrunnelse er for lang") {
                Then("returnerer 400 med valideringsfeil på begrunnelse") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(EnkeltTilgangData(BrukerId(brukerId), "x".repeat(401), gyldigTil))
                    }.andExpect {
                        status { isBadRequest() }
                    }.andReturn().assertProblemDetailBody(
                        mapper,
                        BAD_REQUEST,
                        "En eller flere felter er ugyldige",
                        title = "Validering feilet",
                        fields = listOf("begrunnelse"),
                        requireBody = false
                    )
                }
            }

            When("gyldigtil er i fortiden") {
                Then("returnerer 400 med valideringsfeil på gyldigtil") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(
                            EnkeltTilgangData(BrukerId(brukerId), "En god begrunnelse", LocalDate.now().minusDays(1))
                        )
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }.andReturn().assertProblemDetailBody(
                        mapper,
                        BAD_REQUEST,
                        "En eller flere felter er ugyldige",
                        title = "Validering feilet",
                        fields = listOf("gyldigtil"),
                        requireBody = false
                    )
                }
            }

            When("gyldigtil er mer enn 3 måneder frem i tid") {
                Then("returnerer 400 med valideringsfeil på gyldigtil") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(
                            EnkeltTilgangData(BrukerId(brukerId), "En god begrunnelse", LocalDate.now().plusMonths(4))
                        )
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }.andReturn().assertProblemDetailBody(
                        mapper,
                        BAD_REQUEST,
                        "En eller flere felter er ugyldige",
                        title = "Validering feilet",
                        fields = listOf("gyldigtil"),
                        requireBody = false
                    )
                }
            }
        }
    }
}
