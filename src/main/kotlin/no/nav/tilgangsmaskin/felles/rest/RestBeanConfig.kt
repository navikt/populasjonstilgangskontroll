package no.nav.tilgangsmaskin.felles.rest

import io.micrometer.core.instrument.MeterRegistry
import no.nav.boot.conditionals.ConditionalOnDevOrLocal
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.sekunder
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.service.annotation.DeleteExchange
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PatchExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.annotation.PutExchange
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
    fun httpClientPoolMetrics(registry: MeterRegistry) =
        HttpClientPoolMetrics(registry)

    @Bean
    fun restClientCustomizer() =
        org.springframework.boot.restclient.RestClientCustomizer { c ->
            c.requestInterceptors {
                logbookInterceptor.ifAvailable { interceptor -> it.add(interceptor) }
            }
            c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
        }

    @Bean
    fun httpClientPoolMetricsBinder(metrics: HttpClientPoolMetrics) = object : BeanPostProcessor {
        override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
            when {
                bean is RestClient -> metrics.bind(beanName, bean)
                bean is HttpComponentsClientHttpRequestFactory -> metrics.bind(beanName, bean)
                isHttpExchangeClient(bean) -> metrics.bindClient(beanName)
            }
            return bean
        }

        private fun isHttpExchangeClient(bean: Any): Boolean {
            val types = buildList {
                add(bean.javaClass)
                addAll(bean.javaClass.interfaces)
            }
            return types.any { type ->
                type.methods.any { method ->
                    method.isAnnotationPresent(HttpExchange::class.java) ||
                        method.isAnnotationPresent(GetExchange::class.java) ||
                        method.isAnnotationPresent(PostExchange::class.java) ||
                        method.isAnnotationPresent(PutExchange::class.java) ||
                        method.isAnnotationPresent(PatchExchange::class.java) ||
                        method.isAnnotationPresent(DeleteExchange::class.java)
                }
            }
        }
    }

    @Bean
    fun httpComponentsBuilderCustomizer():
            org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer<org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder> =
        org.springframework.boot.http.client.autoconfigure.ClientHttpRequestFactoryBuilderCustomizer { builder ->
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
