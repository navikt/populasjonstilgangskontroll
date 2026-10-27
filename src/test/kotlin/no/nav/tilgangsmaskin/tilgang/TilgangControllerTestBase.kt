package no.nav.tilgangsmaskin.tilgang

import no.nav.tilgangsmaskin.ansatt.AnsattId

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.mockk
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import no.nav.sikkerhetstjenesten.felles.security.AuthContext.Companion.NAVIDENT
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangController
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.json.ProblemDetailJacksonMixin
import org.springframework.core.MethodParameter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
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

    init {
        beforeSpec {
            RegelMetadata.messageSource = ReloadableResourceBundleMessageSource().apply {
                setBasename("classpath:regel-messages")
            }
            validator = LocalValidatorFactoryBean().also { it.afterPropertiesSet() }
        }

        beforeEach {
            clearAllMocks()
            mockMvc = standaloneSetup(
                TilgangController(regelTjeneste),
                EnkeltTilgangController(enkeltTilgangTjeneste),
                BulkTilgangController(regelTjeneste)
            )
                .setCustomArgumentResolvers(AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(ErrorResponseAdvice())
                .setValidator(validator)
                .build()
        }
    }
}
