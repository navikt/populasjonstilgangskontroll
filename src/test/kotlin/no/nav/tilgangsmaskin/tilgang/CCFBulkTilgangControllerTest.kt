package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.OverstyrbarRegel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.post

class CCFBulkTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("CCF bulk") {

            val avvistBrukerId = "12345678901"
            val specs = setOf(
                BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE),
                BrukerIdOgRegelsett(avvistBrukerId, KOMPLETT_REGELTYPE)
            )

            beforeEach { every { token.erCC } returns true }

            When("bulk/ccf kalles med gyldige specs") {
                Then("returnerer 207 med mix av godkjente og avviste") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(avvistBrukerId)).build()
                    val testRegel = object : OverstyrbarRegel {
                        override val metadata = RegelMetadata(STRENGT_FORTROLIG)
                        override fun evaluer(ansatt: Ansatt, bruker: Bruker) = false
                    }
                    val regelException = RegelException(testAnsatt, testBruker, testRegel)
                    val respons = AggregertBulkRespons(ansattId, setOf(
                        ok(brukerId),
                        EnkeltBulkRespons(regelException)
                    ))
                    every { regelTjeneste.bulkRegler(ansattId, specs) } returns respons
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"},{"brukerId":"$avvistBrukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                    }.andDo { handle(document("ccf-bulk")) }
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
                    val kjerneRespons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))
                    every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns kjerneRespons
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["$brukerId"]"""
                    }.andExpect { status { isMultiStatus() } }
                        .andDo { handle(document("ccf-bulk-regeltype")) }
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
                        .andDo { handle(document("ccf-bulk-for-mange", problemDetailFields)) }
                }
            }

            When("bulk/ccf kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"   ","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect { status { isBadRequest() } }
                        .andDo { handle(document("ccf-bulk-validering", problemDetailFields)) }
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
