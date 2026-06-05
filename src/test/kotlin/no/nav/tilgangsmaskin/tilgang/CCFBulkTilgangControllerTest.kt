package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post

class CCFBulkTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("CCF bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach { every { token.erCC } returns true }

            When("bulk/ccf kalles med gyldige specs") {
                Then("returnerer 207 med resultater") {
                    every { regelTjeneste.bulkRegler(ansattId, specs) } returns respons
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }
                }
            }

            When("bulk/ccf kalles med OBO-token") {
                Then("returnerer 403") {
                    every { token.erCC } returns false
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med KJERNE_REGELTYPE") {
                Then("returnerer 207") {
                    val kjerneSpecs = setOf(BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE))
                    every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["$brukerId"]"""
                    }.andExpect { status { isMultiStatus() } }
                }
            }

            When("bulk/ccf kalles med mer enn 1000 brukere") {
                Then("returnerer 413") {
                    val mangeIds = (1..1001).map { "0${it.toString().padStart(10, '0')}" }
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = mangeIds.joinToString(prefix = "[", postfix = "]") {
                            """{"brukerId":"$it","type":"KOMPLETT_REGELTYPE"}"""
                        }
                    }.andExpect { status { isContentTooLarge() } }
                }
            }

            When("bulk/ccf kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"   ","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/ccf kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/ccf kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["   "]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """[""]"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles uten body") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }
    }
}
