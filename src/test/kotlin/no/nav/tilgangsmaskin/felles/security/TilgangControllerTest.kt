package no.nav.tilgangsmaskin.felles.security

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.APP
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.IDTYP
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.openapi.AggregertBulkRespons
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
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
import java.time.LocalDate.now

@SpringBootTest(classes = [SecurityTestApplication::class])
@AutoConfigureMockMvc
open class TilgangControllerTest(private val mockMvc: MockMvc, private val mapper: JsonMapper) : BehaviorSpec() {

    @MockkBean
    private lateinit var regelTjeneste: RegelTjeneste

    @MockkBean
    private lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste

    init {
        beforeEach {
            justRun { regelTjeneste.kompletteRegler(any(), any()) }
            justRun { regelTjeneste.kjerneregler(any(), any()) }
            every { regelTjeneste.bulkRegler(any(), any()) } returns AggregertBulkRespons(TEST_ANSATT_ID)
            every { enkeltTilgangTjeneste.registrerTilgang(any(), any()) } returns true
        }

        Given("beskyttet endepunkt $PROD_BASE_PATH/komplett") {
            When("request mangler bearer-token") {
                Then("returnerer 401") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(TEST_BRUKER_ID)
                    }.andExpect {
                        status {
                            isUnauthorized()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(UNAUTHORIZED, MANGLER_BEARER_TOKEN)
                }
            }
            When("request har gyldig oauth2-token") {
                Then("returnerer 204") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(TEST_BRUKER_ID)
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }

                    verify {
                        regelTjeneste.kompletteRegler(TEST_ANSATT_ID, TEST_BRUKER_ID.verdi)
                    }
                }
            }

            When("request har token med ugyldig audience") {
                Then("returnerer 401") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        headers {
                            setBearerAuth(jwt(INVALID_AUDIENCE,TEST_ANSATT_ID))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(TEST_BRUKER_ID)
                    }.andExpect {
                        status {
                            isUnauthorized()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(UNAUTHORIZED, MANGLER_BEARER_TOKEN)
                }
            }
        }

        Given("method security på OBO-endepunkter") {
            When("request bruker CCF-token") {
                Then("returnerer 403 for komplett") {
                    mockMvc.post("$PROD_BASE_PATH/komplett") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE, TEST_ANSATT_ID, mapOf(IDTYP to APP)))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(TEST_BRUKER_ID)
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
                }

                Then("returnerer 403 for bulk") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/obo") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE, TEST_ANSATT_ID, mapOf(IDTYP to APP)))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett(TEST_BRUKER_ID.verdi)))
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
                }

                Then("returnerer 403 for overstyr") {
                    val gyldigTil = now().plusMonths(2)
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE, TEST_ANSATT_ID, mapOf(IDTYP to APP, "roles" to listOf(ENKELT))))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(EnkeltTilgangData(TEST_BRUKER_ID, "En god begrunnelse", gyldigTil))
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
                }
            }
        }

        Given("method security på CCF-endepunkter") {
            When("request bruker OBO-token") {
                Then("returnerer 403 for komplett CCF-endepunkt") {
                    mockMvc.post("$PROD_BASE_PATH/ccf/komplett/${TEST_ANSATT_ID.verdi}") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(TEST_BRUKER_ID)
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
                }

                Then("returnerer 403 for bulk CCF-endepunkt") {
                    mockMvc.post("$PROD_BASE_PATH/bulk/ccf/${TEST_ANSATT_ID.verdi}") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID))
                        }
                        contentType = APPLICATION_JSON
                        content = mapper.writeValueAsString(setOf(BrukerIdOgRegelsett(TEST_BRUKER_ID.verdi)))
                    }.andExpect {
                        status {
                            isForbidden()
                        }
                        content {
                            contentType(APPLICATION_PROBLEM_JSON)
                        }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
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

    private fun MvcResult.withBody(httpStatus: HttpStatus, msg: String? = null) =
        assertSoftly(mapper.readValue<ProblemDetail>(response.contentAsByteArray)) {
            status shouldBe httpStatus.value()
            title shouldBe "${httpStatus.value()}"
            msg?.let { detail shouldBe it }
        }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.setProperties(PROD_GCP)
        }
    }
}