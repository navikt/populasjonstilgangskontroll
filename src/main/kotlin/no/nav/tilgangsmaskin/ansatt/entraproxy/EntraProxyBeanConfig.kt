package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyHealthIndicator(cfg: EntraProxyConfig, client: EntraProxyClient) =
        PingableHealthIndicator(cfg, client::ping)
}