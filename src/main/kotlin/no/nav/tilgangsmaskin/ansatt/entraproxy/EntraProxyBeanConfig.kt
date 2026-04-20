package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.PROXY_BASE
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory.builderFor
import org.springframework.web.service.invoker.createClient
import java.net.URI

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient(b: Builder, errorHandler: ErrorHandler)  =
        builderFor(create(client(b, PROXY_BASE, errorHandler)))
            .build()
            .createClient<EntraProxyClient>()

    private fun client(b: Builder, base: URI, errorHandler: ErrorHandler) = b.baseUrl(base)
        .defaultStatusHandler(HttpStatusCode::isError, errorHandler::handle)
        .build()

    @Bean
    fun entraProxyHealthIndicator(a: EntraProxyRestClientAdapter) =
        PingableHealthIndicator(a)
}