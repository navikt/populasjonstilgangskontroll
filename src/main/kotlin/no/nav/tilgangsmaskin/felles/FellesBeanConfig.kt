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
import no.nav.tilgangsmaskin.felles.rest.cache.JsonCacheable
import no.nav.tilgangsmaskin.tilgang.Token
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory.getLogger
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
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.function.Function


@Configuration
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor) : WebMvcConfigurer {

    private val log = getLogger(javaClass)

    @Bean
    fun jacksonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
        it.featuresToEnable(INCLUDE_SOURCE_IN_LOCATION)
        it.mixIns(mapOf(OAuth2AccessTokenResponse::class.java to IgnoreUnknownMixin::class.java))
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin

    @Bean
    fun errorMessageSource() = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:messages")
    }

    @Bean
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor) = RestClientCustomizer { c ->
        c.requestFactory(HttpComponentsClientHttpRequestFactory().apply {
            setConnectTimeout(2000)
            setReadTimeout(2000)
        })
        c.requestInterceptors {
            it.addFirst(interceptor)
            it.add(headerLoggingInterceptor())
        }
    }

    private fun headerLoggingInterceptor() = ClientHttpRequestInterceptor { request, body, next ->
        log.trace("Headers: {}", request.headers)
        if (!body.isEmpty()) {
            log.debug("Body for {} {} : {} ",request.method, request.uri,String(body))
        }
        val response = next.execute(request, body)
        if (!response.statusCode.is2xxSuccessful) {
            log.debug("Response status for {} {}: {}", request.method, request.uri, response.statusCode)
        }
        response
    }

    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry, Function { pjp -> Tags.of("cluster", token.cluster, "method", pjp.signature.name, "client", token.systemNavn) })

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

    @Aspect
    @Component
    class TimingAspect(private val meterRegistry: MeterRegistry) {

        @Around("execution(* no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor.intercept(..))")
        fun timeMethod(joinPoint: ProceedingJoinPoint): Any? {
            val timer = meterRegistry.timer("mslogin", "method", joinPoint.signature.name)
            return timer.recordCallable { joinPoint.proceed() }
        }
    }

    companion object {
        fun headerAddingRequestInterceptor(vararg verdier: Pair<String, String>) =
            ClientHttpRequestInterceptor { request, body, next ->
                verdier.forEach { (key, value) -> request.headers.add(key, value) }
                next.execute(request, body)
            }
    }
}