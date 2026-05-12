package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.LoggingRequestInterceptor
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.boot.actuate.endpoint.SanitizingFunction
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import java.util.function.Function
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FUNCTION


@Configuration
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor) : WebMvcConfigurer {

     @Bean
    fun jackson3Customizer() = JsonMapperBuilderCustomizer {
        it.addMixIn(OAuth2AccessTokenResponse::class.java, IgnoreUnknownMixin::class.java)
       it.enable(INCLUDE_SOURCE_IN_LOCATION)
    }


    @Bean
    fun sanitizingFunction() = SanitizingFunction { data ->
        if (SENSITIVE_KEYS.any { data.key.contains(it, ignoreCase = true) }) data.withValue("******") else data
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:messages")
    }

    @Bean
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor) =
        RestClientCustomizer { c ->
            c.requestFactory(HttpComponentsClientHttpRequestFactory().apply {
                setConnectionRequestTimeout(3000)
                setReadTimeout(5000)
            })
            c.requestInterceptors {
                it.addFirst(interceptor)
                it.add(LoggingRequestInterceptor())
            }
        }



    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry,Function   { pjp -> Tags.of("cluster", token.cluster, "method", pjp.signature.name, "client", token.systemNavn) })

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }


    companion object {

        private val SENSITIVE_KEYS = setOf("password", "secret", "token", "key","credentials", "jwk","private_key")
    }
}

@Retention(BINARY)  // = CLASS in bytecode — enough for JaCoCo
@Target(FUNCTION, CONSTRUCTOR, CLASS)
annotation class Generated

