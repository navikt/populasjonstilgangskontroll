package no.nav.tilgangsmaskin.felles.security

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.OID
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.felles.rest.TokenTypeTeller
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.concurrent.atomic.AtomicBoolean

@SpringBootTest(
    classes = [SecurityTestApplication::class]
)
@AutoConfigureMockMvc
class TilgangControllerMockOAuth2ServerTest : BehaviorSpec() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var regelTjeneste: RegelTjeneste

    @MockkBean(relaxed = true)
    private lateinit var token: Token

    @MockkBean(relaxed = true)
    private lateinit var teller: TokenTypeTeller

    init {
        beforeSpec {
            if (oauthStarted.compareAndSet(false, true)) {
                mockOAuth2.start()
            }
        }

        afterSpec {
            if (oauthStarted.compareAndSet(true, false)) {
                mockOAuth2.shutdown()
            }
        }

        beforeEach {
            every { token.type } returns OBO
            every { token.requiredAnsattId } returns ANSATT_ID
            justRun { regelTjeneste.kompletteRegler(any(), any()) }
        }

        Given("protected endpoint /api/v1/komplett") {
            When("request has no bearer token") {
                Then("returns 401") {
                    mockMvc.post("/api/v1/komplett") {
                        contentType = APPLICATION_JSON
                        content = "\"${BRUKER_ID.verdi}\""
                    }.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }

            When("request has valid oauth2 token") {
                Then("returns 204") {
                    val jwt = mockOAuth2.issueToken(ISSUER_ID, SUBJECT, AUDIENCE,
                        mapOf(
                            NAVIDENT to ANSATT_ID.verdi,
                            OID to "11111111-1111-1111-1111-111111111111")
                    ).serialize()

                    mockMvc.post("/api/v1/komplett") {
                        headers {
                            setBearerAuth(jwt)
                        }
                        contentType = APPLICATION_JSON
                        content = "\"${BRUKER_ID.verdi}\""
                    }.andExpect {
                        status { isNoContent() }
                    }

                    verify { regelTjeneste.kompletteRegler(ANSATT_ID, BRUKER_ID.verdi) }
                }
            }
        }
    }

    companion object {
        private val mockOAuth2 = MockOAuth2Server()
        private val oauthStarted = AtomicBoolean(false)
        private val ANSATT_ID = AnsattId("Z999999")
        private val BRUKER_ID = BrukerId("08526835670")
        private const val ISSUER_ID = "azuread"
        private const val SUBJECT = "subject"
        private const val AUDIENCE = "test-audience"
        private const val ISSUER_URI_PROPERTY = "spring.security.oauth2.resourceserver.jwt.issuer-uri"
        private const val AUDIENCES_PROPERTY = "spring.security.oauth2.resourceserver.jwt.audiences"

        @JvmStatic
        @DynamicPropertySource
        fun configureJwt(registry: DynamicPropertyRegistry) {
            if (oauthStarted.compareAndSet(false, true)) {
                mockOAuth2.start()
            }
            registry.add(ISSUER_URI_PROPERTY) { mockOAuth2.issuerUrl(ISSUER_ID).toString() }
            registry.add(AUDIENCES_PROPERTY) { AUDIENCE }
        }
    }
}
