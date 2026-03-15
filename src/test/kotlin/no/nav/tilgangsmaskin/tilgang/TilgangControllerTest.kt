package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

class TilgangControllerTest : DescribeSpec() {

    @MockK
    lateinit var regelTjeneste: RegelTjeneste
    @MockK
    lateinit var overstyringTjeneste: OverstyringTjeneste
    @MockK
    lateinit var token: Token
    @MockK
    lateinit var teller: TokenTypeTeller

    val ansattId = AnsattId("Z999999")
    val brukerId = "08526835670"

    lateinit var mockMvc: MockMvc

    init {
        beforeSpec {
            MockKAnnotations.init(this@TilgangControllerTest)
        }

        beforeEach {
            clearAllMocks()
            mockMvc = standaloneSetup(TilgangController(regelTjeneste, overstyringTjeneste, token, teller))
                .setValidator(LocalValidatorFactoryBean().also { it.afterPropertiesSet() })
                .build()
            justRun { teller.tell(any<Tags>()) }
        }

        describe("OBO enkeltoppslag") {

            beforeEach {
                every { token.erObo } returns true
                every { token.erCC } returns false
                every { token.ansattId } returns ansattId
            }

            it("komplett - returnerer 204 ved tilgang") {
                justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }

                mockMvc.post("/api/v1/komplett") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("kjerne - returnerer 204 ved tilgang") {
                justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }

                mockMvc.post("/api/v1/kjerne") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("komplett - returnerer 403 ved CCF-token") {
                every { token.erObo } returns false

                mockMvc.post("/api/v1/komplett") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("kjerne - returnerer 403 ved CCF-token") {
                every { token.erObo } returns false

                mockMvc.post("/api/v1/kjerne") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }

        describe("CCF enkeltoppslag") {

            beforeEach {
                every { token.erCC } returns true
                every { token.erObo } returns false
            }

            it("komplett - returnerer 204 ved tilgang") {
                justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }

                mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("kjerne - returnerer 204 ved tilgang") {
                justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }

                mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isNoContent() }
                }
            }

            it("komplett - returnerer 403 ved OBO-token") {
                every { token.erCC } returns false

                mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("kjerne - returnerer 403 ved OBO-token") {
                every { token.erCC } returns false

                mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                    contentType = TEXT_PLAIN
                    content = brukerId
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }

        describe("OBO bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach {
                every { token.erObo } returns true
                every { token.erCC } returns false
                every { token.ansattId } returns ansattId
            }

            it("returnerer 207 med resultater") {
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

            it("returnerer 207 med tom liste ved ingen brukere") {
                mockMvc.post("/api/v1/bulk/obo") {
                    contentType = APPLICATION_JSON
                    content = "[]"
                }.andExpect {
                    status { isMultiStatus() }
                    jsonPath("$.resultater") { isEmpty() }
                }
            }

            it("returnerer 403 ved CCF-token") {
                every { token.erObo } returns false

                mockMvc.post("/api/v1/bulk/obo") {
                    contentType = APPLICATION_JSON
                    content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("returnerer 413 ved for mange brukere") {
                val mangeSpecs = (1..1001).map {
                    BrukerIdOgRegelsett("0${it.toString().padStart(10, '0')}", KOMPLETT_REGELTYPE)
                }

                mockMvc.post("/api/v1/bulk/obo") {
                    contentType = APPLICATION_JSON
                    content = mangeSpecs.joinToString(prefix = "[", postfix = "]") {
                        """{"brukerId":"${it.brukerId}","type":"KOMPLETT_REGELTYPE"}"""
                    }
                }.andExpect {
                    status { isContentTooLarge() }
                }
            }

            it("returnerer 207 med avvist resultat") {
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

        describe("OBO bulk med regelType") {

            val kjerneSpecs = setOf(BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach {
                every { token.erObo } returns true
                every { token.erCC } returns false
                every { token.ansattId } returns ansattId
            }

            it("returnerer 207 med resultater for gitt regeltype") {
                every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons

                mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                    contentType = APPLICATION_JSON
                    content = """["$brukerId"]"""
                }.andExpect {
                    status { isMultiStatus() }
                    jsonPath("$.ansattId") { value(ansattId.verdi) }
                    jsonPath("$.resultater[0].brukerId") { value(brukerId) }
                    jsonPath("$.resultater[0].status") { value(204) }
                }
            }

            it("returnerer 403 ved CCF-token") {
                every { token.erObo } returns false

                mockMvc.post("/api/v1/bulk/obo/KJERNE_REGELTYPE") {
                    contentType = APPLICATION_JSON
                    content = """["$brukerId"]"""
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }

        describe("CCF bulk") {

            val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
            val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

            beforeEach {
                every { token.erCC } returns true
                every { token.erObo } returns false
            }

            it("returnerer 207 med resultater") {
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

            it("returnerer 403 ved OBO-token") {
                every { token.erCC } returns false

                mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                    contentType = APPLICATION_JSON
                    content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("returnerer 207 for gitt regeltype") {
                val kjerneSpecs = setOf(BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE))
                every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons

                mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                    contentType = APPLICATION_JSON
                    content = """["$brukerId"]"""
                }.andExpect {
                    status { isMultiStatus() }
                }
            }

            it("returnerer 413 ved for mange brukere") {
                val mangeIds = (1..1001).map { "0${it.toString().padStart(10, '0')}" }

                mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                    contentType = APPLICATION_JSON
                    content = mangeIds.joinToString(prefix = "[", postfix = "]") {
                        """{"brukerId":"$it","type":"KOMPLETT_REGELTYPE"}"""
                    }
                }.andExpect {
                    status { isContentTooLarge() }
                }
            }
        }

        describe("Overstyr") {

            val gyldigTil = LocalDate.now().plusMonths(2)

            beforeEach {
                every { token.erObo } returns true
                every { token.erCC } returns false
                every { token.ansattId } returns ansattId
            }

            it("returnerer 202 ved vellykket overstyring") {
                every { overstyringTjeneste.overstyr(ansattId, any()) } returns true

                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                }.andExpect {
                    status { isAccepted() }
                }
            }

            it("returnerer 403 ved CCF-token") {
                every { token.erCC } returns true
                every { token.erObo } returns false

                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                }.andExpect {
                    status { isForbidden() }
                }
            }

            it("returnerer 400 ved for kort begrunnelse") {
                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"For kort","gyldigtil":"$gyldigTil"}"""
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("returnerer 400 ved for lang begrunnelse") {
                val langBegrunnelse = "x".repeat(401)
                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"$langBegrunnelse","gyldigtil":"$gyldigTil"}"""
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("returnerer 400 ved gyldigtil i fortiden") {
                val fortid = LocalDate.now().minusDays(1)
                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$fortid"}"""
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            it("returnerer 400 ved gyldigtil mer enn 3 måneder frem i tid") {
                val forLangtFremITid = LocalDate.now().plusMonths(4)
                mockMvc.post("/api/v1/overstyr") {
                    contentType = APPLICATION_JSON
                    content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$forLangtFremITid"}"""
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
