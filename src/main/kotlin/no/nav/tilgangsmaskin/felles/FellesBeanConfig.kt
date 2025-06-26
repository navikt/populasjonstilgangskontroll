package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.FellesRetryListener
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.Include.defaultIncludes
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.reflect.Method
import java.util.function.Function


@Configuration
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor) : WebMvcConfigurer {

    @Bean
    fun jacksonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
        it.featuresToEnable(INCLUDE_SOURCE_IN_LOCATION)
        it.mixIn(OAuth2AccessTokenResponse::class.java, IgnoreUnknownMixin::class.java)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:messages")
    }

    @Bean
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor) = RestClientCustomizer {
        it.requestFactory(HttpComponentsClientHttpRequestFactory())
        it.requestInterceptors {
            it.addFirst(interceptor)
        }
    }


    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry, Function { pjp -> Tags.of("cluster", token.cluster, "method", pjp.signature.name) })

    @Bean
    fun fellesRetryListener() = FellesRetryListener()

    @Bean
    @ConditionalOnNotProd
    fun traceRepository() = InMemoryHttpExchangeRepository()

    @Bean
    @ConditionalOnNotProd
    fun httpExchangesFilter(repository: HttpExchangeRepository) =
        object : HttpExchangesFilter(repository, defaultIncludes()) {
            override fun shouldNotFilter(request: HttpServletRequest) = request.servletPath.contains("monitoring")
        }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }

    companion object {
        fun headerAddingRequestInterceptor(vararg verdier: Pair<String, String>) =
            ClientHttpRequestInterceptor { request, body, next ->
                verdier.forEach { (key, value) -> request.headers.add(key, value) }
                next.execute(request, body)
            }
    }
}