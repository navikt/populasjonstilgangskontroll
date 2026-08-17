package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.StrengtFortroligRegel
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post

class OBOBulkTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach {
                every {
                    token.type
                } returns OBO
            }

            When("bulk/obo kalles med gyldige specs") {
                Then("returnerer 207 med resultater") {
                    every {
                        regelTjeneste.bulkRegler(any(), specs)
                    } returns respons

                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett(brukerId)))
                    }.andExpect {
                        status {
                            isMultiStatus()
                        }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].brukerId") { value(brukerId) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }.andDo {
                        handle(dokumenterMedAuth("obo-bulk"))
                    }
                }
            }

            When("bulk/obo kalles med tom liste") {
                Then("returnerer 207 med tom resultatliste") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = "[]"
                    }.andExpect {
                        status {
                            isMultiStatus()
                        }
                        jsonPath("$.resultater") {
                            isEmpty()
                        }
                    }
                }
            }

            When("bulk/obo kalles med mer enn 1000 brukere") {
                Then("returnerer 413") {
                    val mangeSpecs = (1..1001).map {
                        BrukerIdOgRegelsett(it.toString().padStart(BRUKERID_LENGTH, '0'), KOMPLETT_REGELTYPE)
                    }
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(mangeSpecs)
                    }.andExpect {
                        status {
                            isContentTooLarge()
                        }
                    }
                }
            }

            When("bulk/obo returnerer avvist resultat") {
                Then("returnerer 207 med status 403 og komplett detaljer på avvist bruker") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(brukerId)).build()
                    val testRegel =  StrengtFortroligRegel()
                    val regelException = RegelException(testAnsatt, testBruker, testRegel)
                    val avvistRespons = AggregertBulkRespons(ansattId, setOf(EnkeltBulkRespons(regelException)))
                    every {
                        regelTjeneste.bulkRegler(any(), specs)
                    } returns avvistRespons
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett(brukerId)))
                    }.andExpect {
                        status {
                            isMultiStatus()
                        }
                        jsonPath("$.resultater[0].status") { value(403) }
                        jsonPath("$.resultater[0].detaljer.title") { value("AVVIST_STRENGT_FORTROLIG_ADRESSE") }
                        jsonPath("$.resultater[0].detaljer.brukerIdent") { value(brukerId) }
                        jsonPath("$.resultater[0].detaljer.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].detaljer.kanOverstyres") { value(false) }
                    }.andDo {
                        handle(dokumenterMedAuth("obo-bulk-avvist"))
                    }
                }
            }

            When("bulk/obo kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett("   ")))
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("bulk/obo kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett("")))
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("bulk/obo kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        contentType = APPLICATION_JSON
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }
        }

        Given("OBO bulk med regelType") {

            val annenBrukerId = "12345678901"
            val kjerneSpecs = setOf(
                BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE),
                BrukerIdOgRegelsett(annenBrukerId, KJERNE_REGELTYPE)
            )
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId), ok(annenBrukerId)))

            beforeEach { every { token.type } returns OBO }

            When("bulk/obo/{regelType} kalles med KJERNE_REGELTYPE") {
                Then("returnerer 207 med resultater for gitt regeltype") {
                    every { regelTjeneste.bulkRegler(any(), kjerneSpecs) } returns respons
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = mapper.writeValueAsString(setOf(brukerId, annenBrukerId))
                    }.andExpect {
                        status {
                            isMultiStatus()
                        }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }.andDo { handle(dokumenterMedAuth("obo-bulk-regeltype")) }
                }
            }

            When("bulk/obo/{regelType} kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["   "]"""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("bulk/obo/{regelType} kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """[""]"""
                    }.andExpect {
                        status {
                            isBadRequest()
                        }
                    }
                }
            }

            When("bulk/obo/{regelType} kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo/KJERNE_REGELTYPE") {
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
