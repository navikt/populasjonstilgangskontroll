package no.nav.tilgangsmaskin.felles.rest

import com.nimbusds.jwt.SignedJWT.parse
import io.micrometer.core.instrument.MeterRegistry
import no.nav.boot.conditionals.ConditionalOnDevOrLocal
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.sekunder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.Logbook
import org.zalando.logbook.attributes.AttributeExtractor
import org.zalando.logbook.attributes.HttpAttributes
import org.zalando.logbook.attributes.HttpAttributes.EMPTY
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION


@Configuration
@NoCoverageAnalysis
class RestBeanConfig(
    private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor,
    private val handler: ErrorHandler,
    private val logbookInterceptor: ObjectProvider<LogbookClientHttpRequestInterceptor>,
) : WebMvcConfigurer {

    @Bean
    @ConditionalOnDevOrLocal
    fun logbook(formatter: HttpLogFormatter, jwtClaimsExtractor: AttributeExtractor)  =
        Logbook.builder()
            .bodyFilter { _, body ->
                Regex("""(?<!\d)\d{11}(?!\d)""").replace(body, "<brukerId>")
            }
            .condition(
                exclude(
                    requestTo("**/internal/**"),
                    requestTo("**/monitoring/**"),
                    requestTo("**/actuator/**"),
                    requestTo("https://graph.microsoft.com/v1.0/organization"),
                ),
            )
            .attributeExtractor(NimbusJwtClaimsExtractor())
            .sink(DefaultSink(formatter, DefaultHttpLogWriter()))
            .build()

    @Bean
    fun jackson3Customizer() = JsonMapperBuilderCustomizer {
        it.enable(INCLUDE_SOURCE_IN_LOCATION)
    }

    @Bean
    fun httpClientPoolMetrics(registry: MeterRegistry) =
        HttpClientPoolMetrics(registry)

    @Bean
    fun restClientCustomizer() =
        RestClientCustomizer { c ->
            c.requestInterceptors {
                logbookInterceptor.ifAvailable { interceptor -> it.add(interceptor) }
            }
            c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
        }

    @Bean
    fun httpComponentsBuilderCustomizer():
            ClientHttpRequestFactoryBuilderCustomizer<HttpComponentsClientHttpRequestFactoryBuilder> =
        ClientHttpRequestFactoryBuilderCustomizer { builder ->
            builder
                .withConnectionManagerCustomizer { cm ->
                    cm.setMaxConnTotal(300)
                    cm.setMaxConnPerRoute(50)
                }
                .withConnectionConfigCustomizer { cfg ->
                    cfg.setValidateAfterInactivity(2.sekunder)
                }
        }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }
}

@Component
class NimbusJwtClaimsExtractor : AttributeExtractor {

    override fun extract(request: HttpRequest): HttpAttributes {
        val auth = request.headers.getFirst(AUTHORIZATION) ?: return EMPTY
        return HttpAttributes( parse(auth.removePrefix("Bearer ")).jwtClaimsSet.claims)
    }
}