package no.nav.tilgangsmaskin.felles.security

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.PROD_BASE_PATH
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV_GCP
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDate.now

@SpringBootTest(classes = [SecurityTestApplication::class])
@AutoConfigureMockMvc
class DevEnkeltTilgangSecurityTest(mockMvc: MockMvc, mapper: JsonMapper) : BehaviorSpec() {


    val dto = EnkeltTilgangData(TEST_BRUKER_ID, "En god begrunnelse", now().plusMonths(2))

    val payload = mapper.writeValueAsString(dto)

    @MockkBean
    private lateinit var regel: RegelTjeneste

    @MockkBean
    private lateinit var enkelt: EnkeltTilgangTjeneste

    @MockkBean
    private lateinit var token: Token

    init {
        beforeEach {
            clearMocks(token, regel, enkelt, answers = false)
             every { token.requiredAnsattId } returns TEST_ANSATT_ID
             every { enkelt.registrerTilgang(TEST_ANSATT_ID, dto) } returns true
        }

        Given("role enkelttilgang security chain i dev") {
            When("request har OBO-token med dev role") {
                Then("returnerer 202 for overstyr") {
                    mockMvc.post("$PROD_BASE_PATH/overstyr") {
                        headers {
                            setBearerAuth(jwt(TEST_AUDIENCE,TEST_ANSATT_ID, mapOf(ROLES_CLAIM to listOf("ROLE_DEV"))))
                        }
                        contentType = APPLICATION_JSON
                        content = payload
                    }.andExpect {
                        status {
                            isAccepted()
                        }
                    }

                    verify {
                        enkelt.registrerTilgang(TEST_ANSATT_ID, any())
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.setProperties(DEV_GCP)
        }
    }
}
