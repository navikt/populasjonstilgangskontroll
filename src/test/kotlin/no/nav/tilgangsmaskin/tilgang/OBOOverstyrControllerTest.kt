package no.nav.tilgangsmaskin.tilgang

import io.mockk.every
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.regler.motor.KjerneRegel
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.post
import java.time.LocalDate.now

class OBOOverstyrControllerTest : TilgangControllerTestBase() {

    init {
        Given("Enkelttilgang") {

            val gyldigTil = now().plusMonths(2)

            beforeEach { every { token.erObo } returns true }

            When("engelttilgang kalles med gyldig request og OBO-token") {
                Then("returnerer 202 og dokumenteres i rest docs") {
                    every { enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattId, any(), any()) } returns true
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isAccepted() } }
                        .andDo { handle(dokumenterMedAuth("obo-enkeltilgang")) }
                }
            }

            When("enkelttilgang avvises av kjerneregler") {
                Then("returnerer 403 med komplett ProblemDetail") {
                    val testAnsatt = AnsattBuilder(ansattId).build()
                    val testBruker = BrukerBuilder(BrukerId(brukerId)).build()
                    val testRegel = object : KjerneRegel {
                        override val metadata = RegelMetadata(STRENGT_FORTROLIG)
                        override fun evaluer(ansatt: Ansatt, bruker: Bruker) = false
                    }
                    every { enkeltTilgangTjeneste.registrerEnkeltTilgang(ansattId, any(), any()) } throws
                        RegelException(testAnsatt, testBruker, testRegel)

                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect {
                        status { isForbidden() }
                        jsonPath("$.title") { value("AVVIST_STRENGT_FORTROLIG_ADRESSE") }
                        jsonPath("$.status") { value(403) }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.instance") { isString() }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                        jsonPath("$.kanOverstyres") { value(false) }
                    }
                        .andDo { handle(dokumenterMedAuth("obo-enkeltilgang-avvist", problemDetailFields)) }
                }
            }

            When("enkelttilgang kalles med CCF-token") {
                Then("returnerer 401") {
                    every { token.erCC } returns true
                    every { token.erObo } returns false
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isUnauthorized() } }
                }
            }

            When("enkelttilgang kalles uten token") {
                Then("returnerer 401") {
                    every { token.erCC } returns false
                    every { token.erObo } returns false
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                    }.andExpect {
                        status { isUnauthorized() }
                        jsonPath("$.title") { value("Unauthorized") }
                        jsonPath("$.status") { value(401) }
                        jsonPath("$.instance") { value("$DEFAULT_PREFIX/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(document("obo-enkeltilgang-uten-token", problemDetailFields)) }
                }
            }

            When("begrunnelse er for kort") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"For kort","gyldigtil":"$gyldigTil"}"""
                    }.andExpect {
                        status { isBadRequest() }
                        jsonPath("$.title") { value("Bad Request") }
                        jsonPath("$.status") { value(400) }
                        jsonPath("$.instance") { value("$DEFAULT_PREFIX/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.brukerIdent") { value(brukerId) }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(dokumenterMedAuth("obo-enkeltilgang-begrunnelse-for-kort", problemDetailFields)) }
                }
            }

            When("begrunnelse er for lang") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"${"x".repeat(401)}","gyldigtil":"$gyldigTil"}"""
                    }.andExpect { status { isBadRequest() } }
                }
            }

            When("gyldigtil er i fortiden") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"${now().minusDays(1)}"}"""
                    }.andExpect {
                        status { isBadRequest() }
                        jsonPath("$.title") { value("Bad Request") }
                        jsonPath("$.status") { value(400) }
                        jsonPath("$.instance") { value("$DEFAULT_PREFIX/overstyr") }
                        jsonPath("$.type") { value("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett") }
                        jsonPath("$.navIdent") { value(ansattId.verdi) }
                        jsonPath("$.traceId") { isString() }
                    }
                        .andDo { handle(dokumenterMedAuth("obo-enkeltilgang-validering-gyldighet", problemDetailFields)) }
                }
            }

            When("gyldigtil er mer enn 3 måneder frem i tid") {
                Then("returnerer 400") {
                    mockMvc.post("$DEFAULT_PREFIX/overstyr") {
                        contentType = APPLICATION_JSON
                        content = """{"brukerId":"$brukerId","begrunnelse":"En god begrunnelse","gyldigtil":"${now().plusMonths(4)}"}"""
                    }.andExpect { status { isBadRequest() } }
                }
            }
        }
    }
}

