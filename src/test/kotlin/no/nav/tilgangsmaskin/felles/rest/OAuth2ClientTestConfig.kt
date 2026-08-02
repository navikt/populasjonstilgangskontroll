package no.nav.tilgangsmaskin.felles.rest

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@TestConfiguration
class OAuth2ClientTestConfig {

    @Bean
    fun restClientCustomizer() = RestClientCustomizer { c ->
        c.defaultHeader("Jalla", "42")
        c.defaultStatusHandler(HttpStatusCode::isError, RestDefaultErrorHandler()::handle)
    }

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
        private const val SERVICE_CLIENT_PREFIX = "spring.http.serviceclient"

        @JvmStatic
        fun registerServiceClientBaseUrls(registry: DynamicPropertyRegistry) {
            mapOf(
                "pdl" to "http://pdl-api.pdl",
                "pdlpip" to "http://pdl-api.pdl",
                "pdlgraph" to "http://pdl-api.pdl",
                "skjerming" to "http://skjermede-personer-pip.nom",
                "entraproxy" to "http://entra-proxy.sikkerhetstjenesten",
                "graph" to "https://graph.microsoft.com/v1.0",
                "verge" to "http://repr-api.repr"
            ).forEach { (client, url) ->
                registry.add("$SERVICE_CLIENT_PREFIX.$client.base-url") { url }
            }
        }
    }
}
