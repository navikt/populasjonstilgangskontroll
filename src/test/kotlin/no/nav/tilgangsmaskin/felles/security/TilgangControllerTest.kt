package no.nav.tilgangsmaskin.felles.security

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.OID
import no.nav.tilgangsmaskin.felles.rest.TokenType.CCF
import no.nav.tilgangsmaskin.felles.rest.TokenType.OBO
import no.nav.tilgangsmaskin.felles.security.OAuth2TokenTypeAuthorization.Companion.mismatch
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.BulkTilgangController
import no.nav.tilgangsmaskin.tilgang.TilgangController
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

@SpringBootTest(
    classes = [SecurityTestApplication::class]
)
@AutoConfigureMockMvc
class TilgangControllerTest(private val mockMvc: MockMvc, private val jsonMapper: JsonMapper) : BehaviorSpec() {

    @MockkBean
    private lateinit var regelTjeneste: RegelTjeneste

    @MockkBean
    private lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste

    @MockkBean(relaxed = true)
    private lateinit var token: Token

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
            justRun { regelTjeneste.kompletteRegler(any(), any()) }
            justRun { regelTjeneste.kjerneregler(any(), any()) }
            every { regelTjeneste.bulkRegler(any(), any()) } returns AggregertBulkRespons(ANSATT_ID)
            every { enkeltTilgangTjeneste.registrerTilgang(any(), any()) } returns true
        }

        Given("beskyttet endepunkt /api/v1/komplett") {
            When("request mangler bearer-token") {
                Then("returnerer 401") {
                    mockMvc.post("/api/v1/komplett") {
                                        contentType = APPLICATION_JSON
                                        content = "\"${BRUKER_ID.verdi}\""
                                    }.andExpect {
                                        status { isUnauthorized() }
                                        content { contentType(APPLICATION_PROBLEM_JSON) }
                                    }.andReturn().shouldHaveBoody(UNAUTHORIZED)
                }
            }

            When("request har gyldig oauth2-token") {
                Then("returnerer 204") {
                    every { token.type } returns OBO
                    every { token.requiredAnsattId } returns ANSATT_ID
                    mockMvc.post("/api/v1/komplett") {
                        headers {
                            setBearerAuth(jwt(AUDIENCE))
                        }
                        contentType = APPLICATION_JSON
                        content = "\"${BRUKER_ID.verdi}\""
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }

                    verify { regelTjeneste.kompletteRegler(ANSATT_ID, BRUKER_ID.verdi) }
                }
            }

            When("request har token med ugyldig audience") {
                Then("returnerer 401") {
                    mockMvc.post("/api/v1/komplett") {
                                        headers { setBearerAuth(jwt(INVALID_AUDIENCE)) }
                                        contentType = APPLICATION_JSON
                                        content = "\"${BRUKER_ID.verdi}\""
                                    }.andExpect {
                                        status { isUnauthorized() }
                                        content { contentType(APPLICATION_PROBLEM_JSON) }
                                    }.andReturn().shouldHaveBoody(UNAUTHORIZED)
                }
            }
        }

        Given("method security på OBO-endepunkter") {
            When("request bruker CCF-token") {
                Then("returnerer 403 for komplett, bulk og overstyr") {
                    every { token.type } returns CCF
                    val gyldigTil = LocalDate.now().plusMonths(2)

                    mockMvc.post("/api/v1/komplett") {
                                        headers { setBearerAuth(jwt(AUDIENCE)) }
                                        contentType = APPLICATION_JSON
                                        content = "\"${BRUKER_ID.verdi}\""
                                    }.andExpect {
                                        status {
                                            isForbidden()
                                        }
                                        content {
                                            contentType(APPLICATION_PROBLEM_JSON)
                                        }
                                    }.andReturn().shouldHaveBoody(FORBIDDEN, mismatch(OBO, CCF))

                    mockMvc.post("/api/v1/bulk/obo") {
                                        headers { setBearerAuth(jwt(AUDIENCE)) }
                                        contentType = APPLICATION_JSON
                                        content = """[{"brukerId":"${BRUKER_ID.verdi}","type":"KOMPLETT_REGELTYPE"}]"""
                                    }.andExpect {
                                        status {
                                            isForbidden()
                                        }
                                        content {
                                            contentType(APPLICATION_PROBLEM_JSON)
                                        }
                                    }.andReturn().shouldHaveBoody(FORBIDDEN, mismatch(OBO, CCF))

                    mockMvc.post("/api/v1/overstyr") {
                                        headers {
                                            setBearerAuth(jwt(AUDIENCE))
                                        }
                                        contentType = APPLICATION_JSON
                                        content =
                                            """{"brukerId":"${BRUKER_ID.verdi}","begrunnelse":"En god begrunnelse","gyldigtil":"$gyldigTil"}"""
                                    }.andExpect {
                                        status {
                                            isForbidden()
                                        }
                                        content {
                                            contentType(APPLICATION_PROBLEM_JSON)
                                        }
                                    }.andReturn().shouldHaveBoody(FORBIDDEN, mismatch(OBO, CCF))
                }
            }
        }

        Given("method security på CCF-endepunkter") {
            When("request bruker OBO-token") {
                Then("returnerer 403 for komplett og bulk CCF-endepunkter") {
                    every { token.type } returns OBO

                    mockMvc.post("/api/v1/ccf/komplett/${ANSATT_ID.verdi}") {
                                        headers {
                                            setBearerAuth(jwt(AUDIENCE))
                                        }
                                        contentType = APPLICATION_JSON
                                        content = "\"${BRUKER_ID.verdi}\""
                                    }.andExpect {
                                        status {
                                            isForbidden()
                                        }
                                        content {
                                            contentType(APPLICATION_PROBLEM_JSON)
                                        }
                                    }.andReturn().shouldHaveBoody(FORBIDDEN, mismatch(CCF, OBO))

                    mockMvc.post("/api/v1/bulk/ccf/${ANSATT_ID.verdi}") {
                                        headers {
                                            setBearerAuth(jwt(AUDIENCE))
                                        }
                                        contentType = APPLICATION_JSON
                                        content = """[{"brukerId":"${BRUKER_ID.verdi}","type":"KOMPLETT_REGELTYPE"}]"""
                                    }.andExpect {
                                        status {
                                            isForbidden()
                                        }
                                        content {
                                            contentType(APPLICATION_PROBLEM_JSON)
                                        }
                                    }.andReturn().shouldHaveBoody(FORBIDDEN, mismatch(CCF, OBO))
                }
            }
        }

        Given("åpent endepunkt /v3/api-docs") {
            When("request mangler bearer-token") {
                Then("returnerer 200") {
                    mockMvc.get("/v3/api-docs").andExpect {
                        status {
                            isOk()
                        }
                    }
                }
            }
        }
    }

    private fun jwt(audience: String) = mockOAuth2.issueToken(ISSUER_ID, SUBJECT, audience,
        mapOf(
            NAVIDENT to ANSATT_ID.verdi,
            OID to UUID.randomUUID().toString())
    ).serialize()

    private fun MvcResult.shouldHaveBoody(httpStatus: HttpStatus, expectedDetail: String? = null) =
        assertSoftly(jsonMapper.readValue<ProblemDetail>(response.contentAsByteArray)) {
            status shouldBe httpStatus.value()
            title shouldBe httpStatus.reasonPhrase
            expectedDetail?.let { detail shouldBe it }
        }

    companion object {
        private val mockOAuth2 = MockOAuth2Server()
        private val oauthStarted = AtomicBoolean(false)
        private val ANSATT_ID = AnsattId("Z999999")
        private val BRUKER_ID = BrukerId("08526835670")
        private const val ISSUER_ID = "azuread"
        private const val SUBJECT = "subject"
        private const val AUDIENCE = "test-audience"
        private const val INVALID_AUDIENCE = "invalid-audience"
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

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class, FlywayAutoConfiguration::class])
@Import(OAuth2SecurityBeanConfig::class, TilgangController::class, BulkTilgangController::class, EnkeltTilgangController::class)
class SecurityTestApplication