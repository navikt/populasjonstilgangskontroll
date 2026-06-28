package no.nav.tilgangsmaskin.felles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor
import no.nav.tilgangsmaskin.felles.rest.RestLoggingRequestInterceptor
import no.nav.tilgangsmaskin.tilgang.Token
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.Timeout.ofSeconds
import org.springframework.boot.actuate.endpoint.SanitizingFunction
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import java.time.Clock
import java.time.Clock.systemDefaultZone
import java.time.Instant
import java.util.*
import java.util.function.Function
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FUNCTION


@Configuration
@NoCoverageAnalysis
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

    @Bean("messageSource")
    @ConditionalOnProd
    fun prodMessageSource() =
        ReloadableResourceBundleMessageSource().apply {
            setBasenames("classpath:messages", "classpath:regel-messages", "classpath:openapi-prod-tilgang")
            setDefaultEncoding("UTF-8")
        }

    @Bean("messageSource")
    @ConditionalOnNotProd
    fun notProdMessageSource() =
        ReloadableResourceBundleMessageSource().apply {
            setBasenames(
                "classpath:messages",
                "classpath:regel-messages",
                "classpath:openapi-prod-tilgang",
                "classpath:openapi-dev-ansatt",
                "classpath:openapi-dev-bruker",
                "classpath:openapi-dev-cache",
                "classpath:openapi-dev-enkelt",
                "classpath:openapi-dev-regel",
                "classpath:openapi-dev-skjerming",
                "classpath:openapi-dev-tilgang",
                "classpath:openapi-dev-vergemal",
            )
            setDefaultEncoding("UTF-8")
        }

    @Bean
    fun restClientCustomizer(interceptor: OAuth2ClientRequestInterceptor) =
        RestClientCustomizer { c ->
            val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(
                    ConnectionConfig.custom()
                        .setValidateAfterInactivity(ofSeconds(2))
                        .build()
                )
                .build()
            val httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build()
            c.requestFactory(HttpComponentsClientHttpRequestFactory(httpClient).apply {
                setConnectionRequestTimeout(3000)
                setReadTimeout(5000)
            })
            c.requestInterceptors {
                it.addFirst(interceptor)
                it.add(RestLoggingRequestInterceptor())
            }
        }


    @Bean
    fun clusterAddingTimedAspect(meterRegistry: MeterRegistry, token: Token) =
        TimedAspect(meterRegistry,
            Function { pjp ->
                Tags.of("cluster",
                    "class", pjp.target.javaClass.simpleName,
                    token.cluster,
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

@Retention(BINARY)  // = CLASS in bytecode — enough for JaCoCo
@Target(FUNCTION, CONSTRUCTOR, CLASS)
annotation class Generated
typealias NoCoverageAnalysis = Generated


