package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.instrument.Tags
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons.Companion.ok
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class TilgangControllerTest {

    @MockK
    lateinit var regelTjeneste: RegelTjeneste
    @MockK
    lateinit var overstyringTjeneste: OverstyringTjeneste
    @MockK
    lateinit var token: Token
    @MockK
    lateinit var teller: TokenTypeTeller

    private lateinit var mockMvc: MockMvc

    private val ansattId = AnsattId("Z999999")
    private val brukerId = "08526835670"

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(TilgangController(regelTjeneste, overstyringTjeneste, token, teller))
            .build()
        justRun { teller.tell(any<Tags>()) }
    }

    @Nested
    @DisplayName("OBO enkeltoppslag")
    inner class OboEnkeltoppslag {

        @BeforeEach
        fun setUp() {
            every { token.erObo } returns true
            every { token.erCC } returns false
            every { token.ansattId } returns ansattId
        }

        @Test
        @DisplayName("komplett - returnerer 204 ved tilgang")
        fun komplett() {
            justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }

            mockMvc.post("/api/v1/komplett") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isNoContent() }
            }
        }

        @Test
        @DisplayName("kjerne - returnerer 204 ved tilgang")
        fun kjerne() {
            justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }

            mockMvc.post("/api/v1/kjerne") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isNoContent() }
            }
        }

        @Test
        @DisplayName("komplett - returnerer 403 ved CCF-token")
        fun komplettVedCCFToken() {
            every { token.erObo } returns false

            mockMvc.post("/api/v1/komplett") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isForbidden() }
            }
        }
    }

    @Nested
    @DisplayName("CCF enkeltoppslag")
    inner class CcfEnkeltoppslag {

        @BeforeEach
        fun setUp() {
            every { token.erCC } returns true
            every { token.erObo } returns false
        }

        @Test
        @DisplayName("komplett - returnerer 204 ved tilgang")
        fun komplett() {
            justRun { regelTjeneste.kompletteRegler(ansattId, brukerId) }

            mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isNoContent() }
            }
        }

        @Test
        @DisplayName("kjerne - returnerer 204 ved tilgang")
        fun kjerne() {
            justRun { regelTjeneste.kjerneregler(ansattId, brukerId) }

            mockMvc.post("/api/v1/ccf/kjerne/${ansattId.verdi}") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isNoContent() }
            }
        }

        @Test
        @DisplayName("komplett - returnerer 403 ved OBO-token")
        fun komplettVedOBOToken() {
            every { token.erCC } returns false

            mockMvc.post("/api/v1/ccf/komplett/${ansattId.verdi}") {
                contentType = TEXT_PLAIN
                content = brukerId
            }.andExpect {
                status { isForbidden() }
            }
        }
    }

    @Nested
    @DisplayName("OBO bulk")
    inner class OboBulk {

        private val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
        private val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

        @BeforeEach
        fun setUp() {
            every { token.erObo } returns true
            every { token.erCC } returns false
            every { token.ansattId } returns ansattId
        }

        @Test
        @DisplayName("returnerer 207 med resultater")
        fun bulk() {
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

        @Test
        @DisplayName("returnerer 207 med tom liste ved ingen brukere")
        fun bulkTomListe() {
            mockMvc.post("/api/v1/bulk/obo") {
                contentType = APPLICATION_JSON
                content = "[]"
            }.andExpect {
                status { isMultiStatus() }
                jsonPath("$.resultater") { isEmpty() }
            }
        }

        @Test
        @DisplayName("returnerer 403 ved CCF-token")
        fun bulkVedCCFToken() {
            every { token.erObo } returns false

            mockMvc.post("/api/v1/bulk/obo") {
                contentType = APPLICATION_JSON
                content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        @DisplayName("returnerer 413 ved for mange brukere")
        fun bulkVedForMangeBrukere() {
            val mangeSpecs = (1..1001).map { BrukerIdOgRegelsett("0${it.toString().padStart(10, '0')}", KOMPLETT_REGELTYPE) }

            mockMvc.post("/api/v1/bulk/obo") {
                contentType = APPLICATION_JSON
                content = mangeSpecs.joinToString(prefix = "[", postfix = "]") {
                    """{"brukerId":"${it.brukerId}","type":"KOMPLETT_REGELTYPE"}"""
                }
            }.andExpect {
                status {
                    isContentTooLarge()
                }
            }
        }

        @Test
        @DisplayName("returnerer 207 med avvist resultat")
        fun bulkAvvist() {
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

    @Nested
    @DisplayName("CCF bulk")
    inner class CcfBulk {

        private val specs = setOf(BrukerIdOgRegelsett(brukerId, KOMPLETT_REGELTYPE))
        private val respons = AggregertBulkRespons(ansattId, setOf(ok(brukerId)))

        @BeforeEach
        fun setUp() {
            every { token.erCC } returns true
            every { token.erObo } returns false
        }

        @Test
        @DisplayName("returnerer 207 med resultater")
        fun bulk() {
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

        @Test
        @DisplayName("returnerer 403 ved OBO-token")
        fun bulkVedOBOToken() {
            every { token.erCC } returns false

            mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}") {
                contentType = APPLICATION_JSON
                content = """[{"brukerId":"$brukerId","type":"KOMPLETT_REGELTYPE"}]"""
            }.andExpect {
                status { isForbidden() }
            }
        }

        @Test
        @DisplayName("returnerer 207 for gitt regeltype")
        fun bulkForRegeltype() {
            val kjerneSpecs = setOf(BrukerIdOgRegelsett(brukerId, KJERNE_REGELTYPE))
            every { regelTjeneste.bulkRegler(ansattId, kjerneSpecs) } returns respons

            mockMvc.post("/api/v1/bulk/ccf/${ansattId.verdi}/KJERNE_REGELTYPE") {
                contentType = APPLICATION_JSON
                content = """["$brukerId"]"""
            }.andExpect {
                status { isMultiStatus() }
            }
        }
    }
}

