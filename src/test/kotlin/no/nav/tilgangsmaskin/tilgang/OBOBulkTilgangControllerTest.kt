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
import org.springframework.test.web.servlet.post

class OBOBulkTilgangControllerTest : TilgangControllerTestBase() {

    init {

        Given("OBO bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach { every { token.type } returns TokenType.OBO }

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
                    }.andDo { handle(dokumenterMedAuth("obo-bulk")) }
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
                    every { token.type } returns TokenType.CCF
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
                Then("returnerer 207 med status 403 og komplett detaljer på avvist bruker") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(brukerId)).build()
                    val testRegel = object : OverstyrbarRegel {
                        override val metadata = RegelMetadata(STRENGT_FORTROLIG)
                        override fun evaluer(ansatt: Ansatt, bruker: Bruker) = false
                    }
                    val regelException = RegelException(testAnsatt, testBruker, testRegel)
                    val avvistRespons = AggregertBulkRespons(ansattId, setOf(EnkeltBulkRespons(regelException)))
                    every { regelTjeneste.bulkRegler(ansattId, specs) } returns avvistRespons
                    mockMvc.post("/api/v1/bulk/obo") {
                        contentType = APPLICATION_JSON
                        content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.resultater[0].status") { value(403) }
                        jsonPath("$.resultater[0].detaljer.title") { value("AVVIST_STRENGT_FORTROLIG_ADRESSE") }
                        jsonPath("$.resultater[0].detaljer.brukerIdent") { value(brukerId) }
                        jsonPath("$.resultater[0].detaljer.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].detaljer.kanOverstyres") { value(true) }
                    }.andDo { handle(dokumenterMedAuth("obo-bulk-avvist")) }
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

            val annenBrukerId = "12345678901"
            val kjerneSpecs = setOf(
                BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE),
                BrukerIdOgRegelsett(annenBrukerId, KJERNE_REGELTYPE)
            )
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId), ok(annenBrukerId)))

            beforeEach { every { token.type } returns TokenType.OBO }

            When("bulk/obo/{regelType} kalles med KJERNE_REGELTYPE") {
                Then("returnerer 207 med resultater for gitt regeltype") {
                    every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons
                    mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                        contentType = APPLICATION_JSON; content = """["$brukerId","$annenBrukerId"]"""
                    }.andExpect {
                        status { isMultiStatus() }
                        jsonPath("$.ansattId") { value(ansattId.verdi) }
                        jsonPath("$.resultater[0].status") { value(204) }
                    }.andDo { handle(dokumenterMedAuth("obo-bulk-regeltype")) }
                }
            }

            When("bulk/obo/{regelType} kalles med CCF-token") {
                Then("returnerer 403") {
                    every { token.type } returns TokenType.CCF
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
