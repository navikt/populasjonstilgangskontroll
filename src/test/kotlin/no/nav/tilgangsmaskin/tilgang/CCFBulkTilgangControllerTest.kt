package no.nav.tilgangsmaskin.tilgang

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.sikkerhetstjenesten.felles.notifikasjon.LocalAuditor
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.BulkResponsAggregator
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.KjerneRegel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelMotorLogger
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.StrengtFortroligRegel
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG as STRENGT_FORTROLIG_GRUPPE

class CCFBulkTilgangControllerTest : TilgangControllerTestBase() {
    init {

        Given("CCF bulk") {

            val avvistBrukerId = "12345678901"
            val specs =
                setOf(
                    BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE),
                    BrukerIdOgRegelsett(avvistBrukerId, KOMPLETT_REGELTYPE),
                )

            When("bulk/ccf kalles med gyldige specs") {
                Then("returnerer 207 med mix av godkjente og avviste") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(avvistBrukerId)).build()
                    val testRegel =
                        object : KjerneRegel {
                            override val metadata = RegelMetadata(STRENGT_FORTROLIG)

                            override fun evaluer(
                                ansatt: Ansatt,
                                bruker: Bruker,
                            ) = false
                        }
                    val regelException = RegelException(testAnsatt, testBruker, testRegel)
                    val respons =
                        AggregertBulkRespons(
                            ansattId,
                            setOf(
                                ok(brukerId),
                                EnkeltBulkRespons(regelException),
                            ),
                        )
                    every {
                        regelTjeneste.bulkRegler(any(), specs)
                    } returns respons
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                            contentType = APPLICATION_JSON
                            content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett(brukerId), BrukerIdOgRegelsett(avvistBrukerId)))
                        }.andExpect {
                            status {
                                isMultiStatus()
                            }
                            jsonPath("$.ansattId") { value(ansattId.verdi) }
                            jsonPath("$.resultater[1].brukerId") { value(avvistBrukerId) }
                            jsonPath("$.resultater[1].status") { value(403) }
                        }.andDo {
                            handle(dokumenterMedAuth("ccf-bulk"))
                        }
                }
            }

            When("bulk/ccf kalles med reell RegelTjeneste") {
                Then("regelen avslår identen som krever strengt fortrolig medlemskap") {
                    val ansattTjeneste = mockk<AnsattTjeneste>()
                    val brukerTjeneste = mockk<BrukerTjeneste>()
                    val enkeltTilgangTjeneste = mockk<EnkeltTilgangTjeneste>()
                    val auditor = LocalAuditor()
                    val ansatt = AnsattBuilder(ansattId).build()
                    every { ansattTjeneste.ansatt(ansattId) } returns ansatt
                    every {
                        brukerTjeneste.brukere(setOf(brukerId, avvistBrukerId))
                    } returns
                        setOf(
                            BrukerBuilder(BrukerId(brukerId)).build(),
                            BrukerBuilder(BrukerId(avvistBrukerId))
                                .kreverMedlemskapI(STRENGT_FORTROLIG_GRUPPE)
                                .build(),
                        )
                    every { enkeltTilgangTjeneste.tilganger(ansattId, any()) } returns emptySet()

                    val motor =
                        RegelMotor(
                            RegelSett(KJERNE_REGELTYPE to listOf(StrengtFortroligRegel())),
                            RegelSett(KOMPLETT_REGELTYPE to listOf(StrengtFortroligRegel())),
                            mockk<RegelMotorLogger>(relaxed = true),
                        )
                    val aggregator = BulkResponsAggregator(enkeltTilgangTjeneste, auditor, motor)
                    val realRegelTjeneste =
                        RegelTjeneste(
                            motor,
                            brukerTjeneste,
                            ansattTjeneste,
                            enkeltTilgangTjeneste,
                            auditor,
                            aggregator,
                        )
                    mockMvc = standaloneSetup(BulkTilgangController(realRegelTjeneste)).build()

                    val result =
                        mockMvc
                            .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                                contentType = APPLICATION_JSON
                                content = mapper.writeValueAsString(specs)
                            }.andExpect {
                                status {
                                    isMultiStatus()
                                }
                            }.andReturn()

                    val resultater = mapper.readTree(result.response.contentAsString)["resultater"]
                    resultater.first { it["brukerId"].stringValue() == brukerId }["status"].asInt() shouldBe 204
                    resultater.first { it["brukerId"].stringValue() == avvistBrukerId }["status"].asInt() shouldBe 403
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med KJERNE_REGELTYPE") {
                Then("returnerer 207") {
                    val annenBrukerId = "12345678901"
                    val kjerneSpecs =
                        setOf(
                            BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE),
                            BrukerIdOgRegelsett(annenBrukerId, KJERNE_REGELTYPE),
                        )
                    val kjerneRespons = AggregertBulkRespons(ansattId, setOf(ok(brukerId), ok(annenBrukerId)))
                    every { regelTjeneste.bulkRegler(any(), kjerneSpecs) } returns kjerneRespons
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                            contentType = APPLICATION_JSON
                            content = mapper.writeValueAsString(setOf(brukerId, annenBrukerId))
                        }.andExpect {
                            status {
                                isMultiStatus()
                            }
                        }.andDo {
                            handle(dokumenterMedAuth("ccf-bulk-regeltype"))
                        }
                }
            }

            When("bulk/ccf kalles med mer enn 1000 brukere") {
                Then("returnerer 413") {
                    val mangeIds = (1..1001).map { "0${it.toString().padStart(10, '0')}" }
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                            contentType = APPLICATION_JSON
                            content =
                                mangeIds.joinToString(prefix = "[", postfix = "]") {
                                    """{"brukerId":"$it","type":"KOMPLETT_REGELTYPE"}"""
                                }
                        }.andExpect {
                            status {
                                isContentTooLarge()
                            }
                        }
                }
            }

            When("bulk/ccf kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                            contentType = APPLICATION_JSON
                            content = """[{"brukerId":"   ","type":"KOMPLETT_REGELTYPE"}]"""
                        }.andExpect {
                            status {
                                isBadRequest()
                            }
                        }
                }
            }

            When("bulk/ccf kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                            contentType = APPLICATION_JSON
                            content = """[{"brukerId":"","type":"KOMPLETT_REGELTYPE"}]"""
                        }.andExpect {
                            status {
                                isBadRequest()
                            }
                        }
                }
            }

            When("bulk/ccf kalles uten body") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}") {
                            contentType = APPLICATION_JSON
                        }.andExpect {
                            status {
                                isBadRequest()
                            }
                        }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med blank brukerId") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                            contentType = APPLICATION_JSON
                            content = """["   "]"""
                        }.andExpect {
                            status {
                                isBadRequest()
                            }
                        }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles med tom brukerId") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                            contentType = APPLICATION_JSON
                            content = """[""]"""
                        }.andExpect {
                            status {
                                isBadRequest()
                            }
                        }
                }
            }

            When("bulk/ccf/{ansattId}/{regelType} kalles uten body") {
                Then("returnerer 400") {
                    mockMvc
                        .post("$PROD_BASE_PATH/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
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
