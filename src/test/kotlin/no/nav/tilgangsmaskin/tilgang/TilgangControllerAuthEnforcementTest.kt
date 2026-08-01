package no.nav.tilgangsmaskin.tilgang

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenType.OBO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@WebMvcTest(
    value = [TilgangController::class],
    properties = ["spring.main.allow-bean-definition-overriding=true"]
)
@Import(TilgangControllerAuthEnforcementTest.WebSecurityTestConfig::class)
@ApplyExtension(SpringExtension::class)
class TilgangControllerAuthEnforcementTest : BehaviorSpec() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean(relaxed = true)
    lateinit var regelTjeneste: RegelTjeneste

    @MockkBean(relaxed = true)
    lateinit var token: Token

    @MockkBean(relaxed = true)
    lateinit var teller: TokenTypeTeller

    @MockkBean(relaxed = true)
    lateinit var jwtDecoder: JwtDecoder

    @MockkBean(relaxed = true)
    lateinit var meterRegistry: MeterRegistry

    @MockkBean(relaxed = true)
    lateinit var errorHandler: ErrorHandler

    @TestConfiguration
    @EnableWebSecurity
    class WebSecurityTestConfig

    init {
        Given("TilgangController auth enforcement") {
            When("request mangler token") {
                Then("returneres 403") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"12345678910\""
                    }.andExpect {
                        status { isForbidden() }
                    }
                }
            }
            When("request har gyldig jwt") {
                Then("returneres 204") {
                    every { token.requiredAnsattId } returns AnsattId("Z999999")
                    every { token.type } returns OBO

                    mockMvc.post("/api/v1/komplett") {
                        with(
                            jwt().jwt {
                                it.claim("NAVident", "Z999999")
                                it.claim("oid", "11111111-1111-1111-1111-111111111111")
                            }
                        )
                        contentType = APPLICATION_JSON
                        content = "\"12345678910\""
                    }.andExpect {
                        status { isNoContent() }
                    }
                }
            }
        }
    }
}
