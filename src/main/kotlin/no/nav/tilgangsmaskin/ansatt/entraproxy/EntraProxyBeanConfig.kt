package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.createOAuth2Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient(
        cfg: EntraProxyConfig,
        builder: Builder
    ) = RestClientAdapter.create(builder.baseUrl(cfg.baseUri).build())
        .createOAuth2Client<EntraProxyClient>()

    @Bean
    fun entraProxyHealthIndicator(cfg: EntraProxyConfig, client: EntraProxyClient) =
        PingableHealthIndicator(cfg, client::ping)
}