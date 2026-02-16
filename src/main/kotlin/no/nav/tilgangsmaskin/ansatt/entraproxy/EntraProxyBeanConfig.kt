package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.security.OAuth2ClientConfig.Companion.registrationIdInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraProxyBeanConfig {

    @Bean
    @Qualifier(ENTRAPROXY)
    fun entraProxyRestClient(b: Builder, cfg: EntraProxyConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptor(registrationIdInterceptor(ENTRAPROXY))
            .build()

    @Bean
    fun entraProxyHealthIndicator(a: EntraProxyRestClientAdapter) =
        PingableHealthIndicator(a)

}