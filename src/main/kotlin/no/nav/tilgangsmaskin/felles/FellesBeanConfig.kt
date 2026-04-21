package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import jakarta.servlet.http.HttpServletRequest
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.LoggingRequestInterceptor
import no.nav.tilgangsmaskin.tilgang.Token
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.actuate.endpoint.SanitizingFunction
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.Include.defaultIncludes
import org.springframework.boot.health.actuate.endpoint.StatusAggregator
import org.springframework.boot.health.contributor.Status.DOWN
import org.springframework.boot.health.contributor.Status.UP
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.boot.servlet.actuate.web.exchanges.HttpExchangesFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import tools.jackson.databind.json.JsonMapper
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
    fun kafkaConsumerFactoryCustomizer(mapper: JsonMapper) =
        DefaultKafkaConsumerFactoryCustomizer {
            it.setValueDeserializerSupplier {
                ErrorHandlingDeserializer(JacksonJsonDeserializer(mapper))
            }
        }


    @Bean
    fun outOfServiceIgnoringStatusAggregator() = StatusAggregator {
        when {
            DOWN in it -> DOWN
            else -> UP
        }
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
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor, loggingInterceptor: LoggingRequestInterceptor) =
        RestClientCustomizer { c ->
            c.requestFactory(HttpComponentsClientHttpRequestFactory().apply {
                setConnectionRequestTimeout(2000)
                setReadTimeout(5000)
            })
            c.requestInterceptors {
                it.addFirst(interceptor)
                it.add(loggingInterceptor)
            }
        }



    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry,Function   { pjp -> Tags.of("cluster", token.cluster, "method", pjp.signature.name, "client", token.systemNavn) })

    @Bean
    @ConditionalOnNotProd
    @Generated
    fun traceRepository() = InMemoryHttpExchangeRepository()


    @Bean
    @ConditionalOnNotProd
    @Generated
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

        fun createProxyFactory(cfg: AbstractRestConfig, b: Builder, errorHandler: ErrorHandler) = builderFor(create(b.baseUrl(cfg.baseUri)
            .defaultStatusHandler(HttpStatusCode::isError, errorHandler::handle)
            .build()))
            .build()

        inline fun <reified T : Any> createClient(cfg: AbstractRestConfig, b: Builder, errorHandler: ErrorHandler) =
            createProxyFactory(cfg, b, errorHandler).createClient(T::class.java)

        private val SENSITIVE_KEYS = setOf("password", "secret", "token", "key","credentials", "jwk","private_key")

        fun headerAddingRequestInterceptor(vararg verdier: Pair<String, String>) =
            ClientHttpRequestInterceptor { request, body, next ->
                verdier.forEach { (key, value) -> request.headers.add(key, value) }
                next.execute(request, body)
            }
    }
}

@Retention(BINARY)  // = CLASS in bytecode — enough for JaCoCo
@Target(FUNCTION, CONSTRUCTOR, CLASS)
annotation class Generated