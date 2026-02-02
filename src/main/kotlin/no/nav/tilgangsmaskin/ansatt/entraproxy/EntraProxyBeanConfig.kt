package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.security.OAuth2RestClientInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraProxyBeanConfig {

    @Bean
    @Qualifier(ENTRAPROXY)
    fun entraProxyRestClient(b: Builder, cfg: EntraProxyConfig, authorizedClientManager: OAuth2AuthorizedClientManager) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(OAuth2RestClientInterceptor(authorizedClientManager, "entra-proxy"))
            }
            .build()

    @Bean
    fun entraProxyHealthIndicator(a: EntraProxyRestClientAdapter) =
        PingableHealthIndicator(a)

}