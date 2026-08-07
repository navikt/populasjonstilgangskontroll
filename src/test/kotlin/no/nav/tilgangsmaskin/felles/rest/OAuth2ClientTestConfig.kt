package no.nav.tilgangsmaskin.felles.rest

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@TestConfiguration
class OAuth2ClientTestConfig {

    @Bean
    fun restClientCustomizer(logbookInterceptor: LogbookClientHttpRequestInterceptor) =
        RestClientCustomizer { c ->
            c.requestInterceptors { it.add(logbookInterceptor) }
            c.defaultStatusHandler(HttpStatusCode::isError, RestDefaultErrorHandler()::handle)
        }

    @Bean
    fun logbook() = Logbook.create()

    @Bean
    fun logbookClientHttpRequestInterceptor(logbook: Logbook) = LogbookClientHttpRequestInterceptor(logbook)

    @Bean
    fun restClientGroupCustomizer(
        customizers: ObjectProvider<RestClientCustomizer>,
        env: Environment) =
        RestClientHttpServiceGroupConfigurer { groups ->
            groups.forEachClient { group, builder ->
                env.getRequiredProperty("${SERVICE_CLIENT_PREFIX}.${group.name()}.base-url").let(builder::baseUrl)
                customizers.forEach { it.customize(builder) }
            }
        }

    companion object {
        const val SERVICE_CLIENT_PREFIX = "spring.http.serviceclient"
    }
}
