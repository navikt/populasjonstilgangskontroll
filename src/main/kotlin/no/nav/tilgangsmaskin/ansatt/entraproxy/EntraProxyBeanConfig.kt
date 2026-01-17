package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraProxyBeanConfig {

    @Bean
    @Qualifier(ENTRAPROXY)
    fun proxyRestClient(b: Builder, cfg: EntraProxyConfig) =
        b.baseUrl(cfg.baseUri).build()

    @Bean
//    fun proxyHealthIndicator(a: EntraProxyRestClientAdapter) =  PingableHealthIndicator(a)

}