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
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields
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
        val problemDetailFields: ResponseFieldsSnippet = relaxedResponseFields(
            fieldWithPath("title").type(STRING).description("Avvisningskode eller HTTP status-tittel"),
            fieldWithPath("status").type(NUMBER).description("HTTP-statuskode"),
            fieldWithPath("instance").type(STRING).description("Identifikator for forekomsten (ansattId/brukerId eller request-URI)"),
            fieldWithPath("detail").type(STRING).description("Detaljert beskrivelse av feilen").optional(),
            fieldWithPath("type").type(STRING).description("URI-referanse som identifiserer problemtypen (RFC 9457)").optional(),
            fieldWithPath("brukerIdent").type(STRING).description("Fødselsnummer/d-nummer til bruker det gjelder").optional(),
            fieldWithPath("navIdent").type(STRING).description("NAV-ident til ansatt som ble avvist").optional(),
            fieldWithPath("begrunnelse").type(STRING).description("Menneskelesbar begrunnelse for avvisning").optional(),
            fieldWithPath("traceId").type(STRING).description("OpenTelemetry trace-ID for feilsøking").optional(),
            fieldWithPath("kanOverstyres").type(BOOLEAN).description("Om regelen kan overstyres med enkelttilgang").optional()
        )
    }

    init {
        beforeSpec {
            RegelMetadata.messageSource = ReloadableResourceBundleMessageSource().apply {
                setBasename("classpath:regel-messages")
                setDefaultEncoding("UTF-8")
            }
            MockKAnnotations.init(this@TilgangControllerTestBase)
        }

        beforeEach { case ->
            clearAllMocks()
            restDocumentation.beforeTest(TilgangControllerTestBase::class.java, case.name.name)
            mockMvc = standaloneSetup(TilgangController(regelTjeneste, enkeltTilgangTjeneste, token, TokenTypeGuard(token), konsumentValidator, teller))
                .setControllerAdvice(ProblemDetailExceptionHandler())
                .setValidator(LocalValidatorFactoryBean().also { it.afterPropertiesSet() })
                .apply<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint())
                )
                .build()
            justRun { teller.tell(any<Tags>()) }
            every { token.ansattId } returns ansattId
        }

        afterEach {
            restDocumentation.afterTest()
        }
    }
}
