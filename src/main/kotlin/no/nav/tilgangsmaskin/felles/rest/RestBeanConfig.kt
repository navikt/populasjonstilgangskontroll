package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.sekunder
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.zalando.logbook.core.Conditions.exclude
import org.zalando.logbook.core.Conditions.requestTo
import org.zalando.logbook.HttpLogFormatter
import org.zalando.logbook.Logbook
import org.zalando.logbook.core.DefaultHttpLogWriter
import org.zalando.logbook.core.DefaultSink
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION
import java.time.Duration


@Configuration
@NoCoverageAnalysis
class RestBeanConfig(
    private val ansattIdAddingInterceptor: ConsumerAwareHandlerInterceptor,
    private val handler: ErrorHandler,
    private val logbookInterceptor: ObjectProvider<LogbookClientHttpRequestInterceptor>,
) : WebMvcConfigurer {

    @Bean
    fun logbook(formatter: HttpLogFormatter): Logbook =
        Logbook.builder()
            .condition(exclude(requestTo("/actuator/**")))
            .sink(DefaultSink(formatter, DefaultHttpLogWriter()))
            .build()

    @Bean
    fun jackson3Customizer() = JsonMapperBuilderCustomizer {
        it.enable(INCLUDE_SOURCE_IN_LOCATION)
    }

    @Bean
    fun restClientCustomizer() =
        RestClientCustomizer { c ->
            c.requestFactory(HttpComponentsClientHttpRequestFactory(HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setValidateAfterInactivity(2.sekunder).build())
                    .build())
                .build()).apply {
                setConnectionRequestTimeout(Duration.ofSeconds(3))
                setReadTimeout(Duration.ofSeconds(5))
            })
            c.requestInterceptors {
                if (!isProd) {
                    logbookInterceptor.ifAvailable { interceptor -> it.add(interceptor) }
                }
            }
            c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
        }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ansattIdAddingInterceptor)
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(APPLICATION_JSON)
    }
}
