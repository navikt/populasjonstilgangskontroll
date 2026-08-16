package no.nav.tilgangsmaskin.felles.rest

import no.nav.boot.conditionals.ConditionalOnDevOrLocal
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder
import org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.zalando.logbook.HeaderFilter.none
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.Logbook
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION


@Configuration
@NoCoverageAnalysis
class RestBeanConfig(
    private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor,
    private val handler: ErrorHandler,
    private val logbookInterceptor: org.springframework.beans.factory.ObjectProvider<LogbookClientHttpRequestInterceptor>,
) : WebMvcConfigurer {

    @Bean
    @ConditionalOnDevOrLocal
    fun logbook(formatter: HttpLogFormatter): Logbook =
        Logbook.builder()
            .headerFilter(none())
            .condition(exclude(
                requestTo("**/internal/**"),
                requestTo("**/monitoring/**"),
                requestTo("**/actuator/**")))
            .sink(DefaultSink(formatter, DefaultHttpLogWriter()))
            .build()

    @Bean
    fun jackson3Customizer() = org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer {
        it.enable(INCLUDE_SOURCE_IN_LOCATION)
    }

    @Bean
    fun httpClientPoolMetrics(registry: io.micrometer.core.instrument.MeterRegistry) = HttpClientPoolMetrics(registry)

    @Bean
    fun restClientCustomizer() =
        RestClientCustomizer { c ->
            c.requestInterceptors {
                logbookInterceptor.ifAvailable { interceptor -> it.add(interceptor) }
            }
            c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
        }

    @Bean
    fun httpComponentsBuilderCustomizer(metrics: HttpClientPoolMetrics):
            ClientHttpRequestFactoryBuilderCustomizer<HttpComponentsClientHttpRequestFactoryBuilder> =
        ClientHttpRequestFactoryBuilderCustomizer { builder ->
            builder
                .withCustomizer(metrics::bind)
                .withConnectionManagerCustomizer { cm ->
                    cm.setMaxConnTotal(300)
                    cm.setMaxConnPerRoute(50)
                }
                .withConnectionConfigCustomizer { cfg ->
                    cfg.setValidateAfterInactivity(org.apache.hc.core5.util.TimeValue.ofSeconds(2))
                }
        }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }
}
