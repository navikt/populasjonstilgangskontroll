package no.nav.tilgangsmaskin.felles

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.RestLoggingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.sekunder
import no.nav.tilgangsmaskin.tilgang.Token
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.Timeout
import org.apache.hc.core5.util.Timeout.of
import org.apache.hc.core5.util.Timeout.ofSeconds
import org.springframework.boot.actuate.endpoint.SanitizingFunction
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import java.time.Clock
import java.time.Clock.systemDefaultZone
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import java.util.function.Function
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FUNCTION


@Configuration
@NoCoverageAnalysis
class FellesBeanConfig(private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor, private val handler: ErrorHandler) : WebMvcConfigurer {

    @Bean
    fun jackson3Customizer() = JsonMapperBuilderCustomizer {
        it.enable(INCLUDE_SOURCE_IN_LOCATION)
    }

    @Bean
    fun sanitizingFunction() = SanitizingFunction { data ->
        if (SENSITIVE_KEYS.any { data.key.contains(it, ignoreCase = true) }) data.withValue("******") else data
    }

    @Bean
    fun restClientCustomizer() =
        RestClientCustomizer { c ->
            c.requestFactory(HttpComponentsClientHttpRequestFactory(HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(
                        ConnectionConfig.custom()
                            .setValidateAfterInactivity(2.sekunder)
                            .build()
                    )
                    .build())
                .build()).apply {
                setConnectionRequestTimeout(Duration.ofSeconds(3))
                setReadTimeout(Duration.ofSeconds(5))
            })
            c.requestInterceptors {
                it.add(RestLoggingRequestInterceptor())
            }
            c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
        }

    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry,
            Function { pjp ->
                Tags.of("cluster",
                    token.cluster,
                    "class", pjp.target.javaClass.simpleName,
                    "method",
                    pjp.signature.name,
                    "client",
                    token.systemNavn)
            })

    /**
     * Sentral klokke-bønne. Injiser `Clock` i komponenter som trenger nåtid
     * (i stedet for `Instant.now()` / `LocalDate.now()` direkte) — så blir tid testbart
     * med `Clock.fixed(...)` eller en mutbar test-klokke.
     */
    @Bean
    fun clock(): Clock = systemDefaultZone()

    /**
     * Brukes av JPA-auditing (@CreatedDate / @LastModifiedDate) og er knyttet
     * via `@EnableJpaAuditing(dateTimeProviderRef = AUDITING_TIME_PROVIDER)`.
     */
    @Bean(AUDITING_TIME_PROVIDER)
    fun auditingDateTimeProvider(clock: Clock) =
        DateTimeProvider { Optional.of(Instant.now(clock)) }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }


    companion object {
        const val AUDITING_TIME_PROVIDER = "auditingDateTimeProvider"
        private val SENSITIVE_KEYS = setOf("password", "secret", "token", "key", "credentials", "jwk", "private_key")
    }
}

@Retention(BINARY)  
@Target(FUNCTION, CONSTRUCTOR, CLASS)
annotation class Generated
typealias NoCoverageAnalysis = Generated

