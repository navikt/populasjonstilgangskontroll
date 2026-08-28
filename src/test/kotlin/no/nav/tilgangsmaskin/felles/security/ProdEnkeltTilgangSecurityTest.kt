package no.nav.tilgangsmaskin.felles.security

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ProblemDetail
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.time.LocalDate.now

@SpringBootTest(classes = [SecurityTestApplication::class])
@AutoConfigureMockMvc
@ActiveProfiles(PROD_GCP)
open class ProdEnkeltTilgangSecurityTest(private val mockMvc: MockMvc, private val mapper: JsonMapper) : BehaviorSpec() {

    @MockkBean
    private lateinit var regelTjeneste: RegelTjeneste

    @MockkBean
    private lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste

    init {
        beforeEach {
            clearMocks(regelTjeneste, enkeltTilgangTjeneste, answers = false)
            every { enkeltTilgangTjeneste.registrerTilgang(TEST_ANSATT_ID, any()) } returns true
        }

        val payload = mapper.writeValueAsString(
            EnkeltTilgangData(TEST_BRUKER_ID, "En god begrunnelse", now().plusMonths(2))
        )
        Given("role sjekk") {
            When("jwt har claim med tillatt role") {
                Then("returnerer 204 for enkelttilgang") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID, mapOf(ROLES_CLAIM to listOf(ENKELT))))
                        }
                        contentType = APPLICATION_JSON
                        content = payload
                    }.andExpect {
                        status {
                            isNoContent()
                        }
                    }

                    verify {
                        enkeltTilgangTjeneste.registrerTilgang(TEST_ANSATT_ID, any())
                    }
                }
            }

            When("jwt har claim med tillatt og ugyldig role") {
                Then("returnerer 204 for enkelttilgang") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE, TEST_ANSATT_ID, mapOf(ROLES_CLAIM to listOf(ENKELT, UTILGJENGELIG))))
                        }
                        contentType = APPLICATION_JSON
                        content = payload
                    }.andExpect {
                        status { isNoContent() }
                    }

                    verify {
                        enkeltTilgangTjeneste.registrerTilgang(TEST_ANSATT_ID, any())
                    }
                }
            }

            When("jwt har claim med ugyldig role") {
                Then("avvises med 403 for enkelttilgang") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE, TEST_ANSATT_ID, mapOf(ROLES_CLAIM to listOf(UTILGJENGELIG))))
                        }
                        contentType = APPLICATION_JSON
                        content = payload
                    }.andExpect {
                        status { isForbidden() }
                        content { contentType(APPLICATION_PROBLEM_JSON) }
                    }.andReturn().withBody(FORBIDDEN, "Access Denied")
                }
            }

            When("jwt har claim uten role") {
                Then("avvises med 403 for enkelttilgang") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID))
                        }
                        contentType = APPLICATION_JSON
                        content = payload
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
