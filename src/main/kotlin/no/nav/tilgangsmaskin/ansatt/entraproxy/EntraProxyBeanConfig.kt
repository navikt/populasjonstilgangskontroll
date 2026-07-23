package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.TexasTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient(cfg: EntraProxyConfig, builder: Builder, texas: TexasTokenProvider,
                         @Value("\${texas.scope.entraproxy}") scope: String) =
        createClient<EntraProxyClient>(cfg, builder, interceptors = arrayOf(texas.interceptorFor(scope)))

    @Bean
    fun entraProxyHealthIndicator(cfg: EntraProxyConfig, client: EntraProxyClient) =
        PingableHealthIndicator(cfg, client::ping)
}