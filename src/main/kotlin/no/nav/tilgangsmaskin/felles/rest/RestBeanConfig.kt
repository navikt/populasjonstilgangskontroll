package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import no.nav.boot.conditionals.ConditionalOnDevOrLocal
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.GROUPS
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.NAVIDENT
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.OID
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
import org.zalando.logbook.HeaderFilter.none
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
import org.zalando.logbook.core.attributes.JwtAllMatchingClaimsExtractor
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import tools.jackson.databind.json.JsonMapper
import java.util.Base64


@Configuration
@NoCoverageAnalysis
class RestBeanConfig(
    private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor,
    private val handler: ErrorHandler,
    private val logbookInterceptor: ObjectProvider<LogbookClientHttpRequestInterceptor>,
) : WebMvcConfigurer {

    @Bean
    @ConditionalOnDevOrLocal
    fun jwtClaimsExtractor(jsonMapper: JsonMapper) =
        PrettyPrintingJwtClaimsAttributeExtractor(jsonMapper)

    @Bean
    @ConditionalOnDevOrLocal
    fun logbook(formatter: HttpLogFormatter, jwtClaimsExtractor: AttributeExtractor)  =
        Logbook.builder()
            .condition(
                exclude(
                    requestTo("**/internal/**"),
                    requestTo("**/monitoring/**"),
                    requestTo("**/actuator/**"),
                    requestTo("https://graph.microsoft.com/v1.0/organization"),
                ),
            )
            .attributeExtractor(jwtClaimsExtractor)
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
class PrettyPrintingJwtClaimsAttributeExtractor(
    private val jsonMapper: JsonMapper,
) : AttributeExtractor {

    override fun extract(request: HttpRequest): HttpAttributes {
        val auth = request.headers.getFirst(AUTHORIZATION) ?: return EMPTY
        val token = auth.removePrefix("Bearer ").takeIf { it != auth } ?: return EMPTY
        val parts = token.split(".")
        if (parts.size != 3) return EMPTY

        val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))
        val payload = jsonMapper.readTree(payloadJson)

        val claims = linkedMapOf<String, Any?>(
            OID to payload[OID]?.asString(),
            NAVIDENT to payload[NAVIDENT]?.asString(),
            AZP_NAME to payload[AZP_NAME]?.asString(),
            GROUPS to payload[GROUPS]?.takeIf { it.isArray }?.map { it.asString() },
        ).filterValues { it != null }

        return HttpAttributes(mapOf("jwt" to claims))
    }
}