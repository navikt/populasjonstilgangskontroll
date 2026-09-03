package no.nav.tilgangsmaskin.tilgang

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.security.AuthContext.Companion.NAVIDENT
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.TYPE_URI
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.HOST
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.json.ProblemDetailJacksonMixin
import org.springframework.core.MethodParameter
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.JsonFieldType.BOOLEAN
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal

abstract class TilgangControllerTestBase : BehaviorSpec() {

    protected fun dokumenterMedAuth(identifier: String, vararg snippets: Snippet) =
        document(
            identifier,
            preprocessRequest(
                modifyHeaders()
                    .set(HOST, "tilgangsmaskin.intern.nav.no")
                    .set(AUTHORIZATION, "Bearer ey....."),
                prettyPrint()
            ),
            preprocessResponse(prettyPrint()),
            *snippets
        )

    protected val mapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .addMixIn(ProblemDetail::class.java, ProblemDetailJacksonMixin::class.java)
        .build()

    protected val regelTjeneste: RegelTjeneste = mockk()

    protected val enkeltTilgangTjeneste: EnkeltTilgangTjeneste = mockk()

    protected val ansattId = AnsattId("Z999999")
    protected val brukerId = "08526835670"

    protected val cache: CacheOperations = mockk(relaxed = true)


    protected lateinit var mockMvc: MockMvc

    private val restDocumentation = ManualRestDocumentation()
    private lateinit var validator: LocalValidatorFactoryBean
    private val principal = DefaultOAuth2AuthenticatedPrincipal(
        mapOf(NAVIDENT to ansattId.verdi),
        emptyList()
    )

    @RestControllerAdvice
    private class ErrorResponseAdvice {
        @ExceptionHandler(ErrorResponseException::class)
        fun handle(ex: ErrorResponseException) =
            org.springframework.http.ResponseEntity.status(ex.statusCode)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(ex.body)
    }

    private inner class AuthenticationPrincipalArgumentResolver : HandlerMethodArgumentResolver {
        override fun supportsParameter(parameter: MethodParameter) =
            parameter.hasParameterAnnotation(AuthenticationPrincipal::class.java) &&
                parameter.parameterType == OAuth2AuthenticatedPrincipal::class.java

        override fun resolveArgument(
            parameter: MethodParameter,
            mavContainer: ModelAndViewContainer?,
            webRequest: NativeWebRequest,
            binderFactory: WebDataBinderFactory?
        ) = principal
    }

    protected companion object {
        private val avvisningskoder = AvvisningsKode.entries.joinToString(", ") { it.name }

        val problemDetailFields = relaxedResponseFields(
            fieldWithPath("title").type(STRING)
                .description("Avvisningskode, En av: $avvisningskoder").optional(),
            fieldWithPath("status").type(NUMBER).description("HTTP-statuskode").optional(),
            fieldWithPath("detail").type(STRING).description("Beskrivelse av feilen").optional(),
            fieldWithPath("instance").type(STRING).description("ansattId/brukerId").optional(),
            fieldWithPath("type").type(STRING).description("Link til utdypende info: $TYPE_URI").optional(),
            fieldWithPath("brukerIdent").type(STRING).description("Identen til bruker").optional(),
            fieldWithPath("navIdent").type(STRING).description("NAV-identen til den ansatte").optional(),
            fieldWithPath("begrunnelse").type(STRING).description("Menneskelesbar begrunnelse for avvisning").optional(),
            fieldWithPath("traceId").type(STRING).description("OTEL trace-ID for feilsøking").optional(),
            fieldWithPath("kanOverstyres").type(BOOLEAN).description("Om regelen kan overstyres med enkelttilgang").optional(),
            fieldWithPath("feil").type(ARRAY).description("Liste over valideringsfeil for felter").optional(),
            fieldWithPath("feil[].felt").type(STRING).description("Feltnavn som feilet").optional(),
            fieldWithPath("feil[].melding").type(STRING).description("Feilmelding for feltet").optional()
        )
    }

    init {
        beforeSpec {
            RegelMetadata.messageSource = ReloadableResourceBundleMessageSource().apply {
                setBasename("classpath:regel-messages")
            }
            validator = LocalValidatorFactoryBean().also { it.afterPropertiesSet() }
        }

        beforeEach { case ->
            clearAllMocks()
            restDocumentation.beforeTest(TilgangControllerTestBase::class.java, case.name.name)
            mockMvc = standaloneSetup(
                TilgangController(regelTjeneste, cache),
                EnkeltTilgangController(enkeltTilgangTjeneste),
                BulkTilgangController(regelTjeneste)
            )
                .setCustomArgumentResolvers(AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(ErrorResponseAdvice())
                .setValidator(validator)
                .apply<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation)
                    .uris()
                    .withScheme("https")
                    .withHost("tilgangsmaskin.intern.nav.no")
                    .and()
                    .operationPreprocessors()
                    .withRequestDefaults(
                        modifyHeaders()
                            .set(HOST, "tilgangsmaskin.intern.nav.no"),
                        prettyPrint()
                    )
                    .withResponseDefaults(prettyPrint())
                )
                .build()
        }

        afterEach {
            restDocumentation.afterTest()
        }
    }
}
