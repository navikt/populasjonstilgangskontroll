package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post

class OBOBulkTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach { every { token.erObo } returns true }

            When("bulk/obo kalles med gyldige specs") {
                Then("returnerer 207 med resultater") {
                    every { regelTjeneste.bulkRegler(ansattId, specs) } returns respons
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].brukerId") { value(brukerId) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }
                }
            }

            When("bulk/obo kalles med tom liste") {
                Then("returnerer 207 med tom resultatliste") {
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON; content = "[]"
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.resultater") { isEmpty() }
                    }
                }
            }

            When("bulk/obo kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("bulk/obo kalles med mer enn 1000 brukere") {
                Then("returnerer 413") {
                    val mangeSpecs = (1..1001).map {
                        BrukerIdOgRegelsett("0${it.toString().padStart(10, '0')}", KOMPLETT_REGELTYPE)
                    }
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = mangeSpecs.joinToString(prefix = "[", postfix = "]") {
                            """{"brukerId":"${it.brukerId}","type":"KOMPLETT_REGELTYPE"}"""
                        }
                    }.andExpect { status { isContentTooLarge() } }
                }
            }

            When("bulk/obo returnerer avvist resultat") {
                Then("returnerer 207 med status 403 på avvist bruker") {
                    val avvistRespons = AggregertBulkRespons(ansattId, setOf(EnkeltBulkRespons(brukerId, FORBIDDEN)))
                    every { regelTjeneste.bulkRegler(ansattId, specs) } returns avvistRespons
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.resultater[0].status") { value(403) }
                    }
                }
            }

            When("bulk/obo kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"   ","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/obo kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/obo kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }

        Given("OBO bulk med regelType") {

            val kjerneSpecs = setOf(BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach { every { token.erObo } returns true }

            When("bulk/obo/{regelType} kalles med KJERNE_REGELTYPE") {
                Then("returnerer 207 med resultater for gitt regeltype") {
                    every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["$brukerId"]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }
                }
            }

            When("bulk/obo/{regelType} kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.erObo } returns false
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["$brukerId"]"""
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("bulk/obo/{regelType} kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["   "]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/obo/{regelType} kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """[""]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/obo/{regelType} kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }
    }
}
