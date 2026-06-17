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
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangKonsumentValidator
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

abstract class TilgangControllerTestBase : BehaviorSpec() {

    @MockK
    protected lateinit var regelTjeneste: RegelTjeneste

    @MockK
    protected lateinit var enkeltTilgangTjeneste: EnkeltTilgangTjeneste

    @MockK(relaxed = true)
    protected lateinit var token: Token

    @MockK
    protected lateinit var teller: TokenTypeTeller

    @MockK(relaxed = true)
    protected lateinit var konsumentValidator: EnkeltTilgangKonsumentValidator

    protected val ansattId = AnsattId("Z999999")
    protected val brukerId = "08526835670"

    protected lateinit var mockMvc: MockMvc

    private val restDocumentation = ManualRestDocumentation()

    @RestControllerAdvice
    private class ProblemDetailExceptionHandler : ResponseEntityExceptionHandler()

    protected companion object {
        val problemDetailFields: ResponseFieldsSnippet = responseFields(
            fieldWithPath("type").type(STRING).description("URI-referanse som identifiserer problemtypen").optional(),
            fieldWithPath("title").type(STRING).description("Kort beskrivelse av feilkategorien (HTTP status)"),
            fieldWithPath("status").type(NUMBER).description("HTTP-statuskode"),
            fieldWithPath("detail").type(STRING).description("Detaljert beskrivelse av feilen"),
            fieldWithPath("instance").type(STRING).description("URI som identifiserer den spesifikke forekomsten av feilen")
        )
    }

    init {
        beforeSpec { MockKAnnotations.init(this@TilgangControllerTestBase) }

        beforeEach { case ->
            clearAllMocks()
            restDocumentation.beforeTest(TilgangControllerTestBase::class.java, case.name.name)
            mockMvc = standaloneSetup(TilgangController(regelTjeneste, enkeltTilgangTjeneste, token, TokenTypeGuard(token), konsumentValidator, teller))
                .setControllerAdvice(ProblemDetailExceptionHandler())
                .setValidator(LocalValidatorFactoryBean().also { it.afterPropertiesSet() })
                .apply<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation))
                .build()
            justRun { teller.tell(any<Tags>()) }
            every { token.ansattId } returns ansattId
        }

        afterEach {
            restDocumentation.afterTest()
        }
    }
}
