package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient( cfg: EntraProxyConfig,b: Builder, errorHandler: ErrorHandler) =
        createClient<EntraProxyClient>(cfg, b, errorHandler)

    @Bean
    fun entraProxyHealthIndicator(a: EntraProxyRestClientAdapter) =
        PingableHealthIndicator(a)
}