package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.LoggingRetryListener
import no.nav.tilgangsmaskin.tilgang.Token
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.Include.defaultIncludes
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.function.Function
import kotlin.math.log
import kotlin.text.Charsets.UTF_8


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
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor, loggingInterceptor: LoggingRequestInterceptor) = RestClientCustomizer { c ->
        c.requestFactory(HttpComponentsClientHttpRequestFactory().apply {
            setConnectTimeout(2000)
            setReadTimeout(2000)
        })
        c.requestInterceptors {
            it.addFirst(interceptor)
            it.add(loggingInterceptor)
        }
    }

    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry, Function { pjp -> Tags.of("cluster", token.cluster, "method", pjp.signature.name, "client", token.systemNavn) })

    @Bean
    fun fellesRetryListener() = LoggingRetryListener()

    @Bean
    @ConditionalOnNotProd
    fun traceRepository() = InMemoryHttpExchangeRepository()

    @Bean
    @ConditionalOnNotProd
    fun auditRepository() = InMemoryAuditEventRepository()


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
        fun timeMethod(joinPoint: ProceedingJoinPoint) = Timer.builder("mslogin")
            .description("Timer med histogram for mslogin")
            .tags("method", joinPoint.signature.name)
            .publishPercentileHistogram()
            .register(meterRegistry).recordCallable { joinPoint.proceed() }
    }

    companion object {
        fun headerAddingRequestInterceptor(vararg verdier: Pair<String, String>) =
            ClientHttpRequestInterceptor { request, body, next ->
                verdier.forEach { (key, value) -> request.headers.add(key, value) }
                next.execute(request, body)
            }
    }
}

@Component
@ConditionalOnNotProd
class ResponseLoggingFilter : OncePerRequestFilter() {

    private val log = getLogger(javaClass)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) =
        with(CustomResponseWrapper(res)) {
            chain.doFilter(req, this)
            log.trace("Response status {} med body: {} fra {}",status,getContentAsString(res.characterEncoding), req.requestURI)
            copyBodyToResponse()
        }
    private class CustomResponseWrapper(response: HttpServletResponse) : ContentCachingResponseWrapper(response) {

        fun getContentAsString(characterEncoding: String) =
            runCatching { String(contentAsByteArray, charset(characterEncoding)) }.getOrNull()
    }
}

@Component
class LoggingRequestInterceptor : ClientHttpRequestInterceptor {
    private val log = getLogger(javaClass)
    override fun intercept(req: HttpRequest, body: ByteArray, exec: ClientHttpRequestExecution) =
        BufferingClientHttpResponse(exec.execute(req, body)).also {
            log.debug("Response {} fra {} med status {}", it.getBodyAsString(UTF_8),  req.uri, it.statusCode)
        }
    class BufferingClientHttpResponse(private val res: ClientHttpResponse) : ClientHttpResponse by res {
        private val bodyBytes = res.body.readBytes()
        override fun getBody() = ByteArrayInputStream(bodyBytes)
        fun getBodyAsString(charset: Charset) = runCatching { bodyBytes.toString(charset) }.getOrNull()
        @Deprecated("Deprecated in Java")
        override fun getRawStatusCode() = res.rawStatusCode
    }
}