package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.micrometer.core.instrument.Tags
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.time.LocalDate

class TilgangControllerTest : BehaviorSpec() {

    @MockK lateinit var regelTjeneste: RegelTjeneste
    @MockK lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste
    @MockK(relaxed = true) lateinit var token: Token
    @MockK lateinit var teller: TokenTypeTeller

    val ansattId = AnsattId("Z999999")
    val brukerId = "08526835670"

    lateinit var mockMvc: MockMvc

    init {
        beforeSpec { MockKAnnotations.init(this@TilgangControllerTest) }

        beforeEach {
            clearAllMocks()
            mockMvc = standaloneSetup(TilgangController(regelTjeneste, enkeltTilgangTjeneste, token, TokenTypeGuard(token), teller))
                .setValidator(LocalValidatorFactoryBean().also { it.afterPropertiesSet() })
                .build()
            justRun { teller.tell(any<Tags>()) }
            every { token.ansattId } returns ansattId
        }

        Given("OBO enkeltoppslag") {

            beforeEach { every { token.erObo } returns true }

            When("komplett kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/komplett") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/kjerne") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
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
        }

        Given("CCF enkeltoppslag") {

            beforeEach { every { token.erCC } returns true }

            When("komplett kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
                }
            }

            When("kjerne kalles med CCF-token") {
                Then("returnerer 204 ved tilgang") {
                    justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isNoContent() } }
                }
            }

            When("komplett kalles med OBO-token") {
                Then("returnerer 403") {
                    every { token.erCC } returns false
                    mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isForbidden() } }
                }
            }

            When("kjerne kalles med OBO-token") {
                Then("returnerer 403") {
                    every { token.erCC } returns false
                    mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                        contentType = TEXT_PLAIN; content = brukerId
                    }.andExpect { status { isForbidden() } }
                }
            }
        }

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
        }

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
        }

        Given("Overstyr") {

            val gyldigTil = LocalDate.now().plusMonths(2)

            beforeEach { every { token.erObo } returns true }

            When("overstyr kalles med gyldig request og OBO-token") {
                Then("returnerer 202") {
                    every { enkeltTilgangTjeneste.overstyr(ansattId, any()) } returns true
                    mockMvc.post("/api/v1/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isAccepted() } }
                }
            }

            When("overstyr kalles med CCF-token") {
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