package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter.create
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient(b: Builder, cfg: EntraProxyConfig) = HttpServiceProxyFactory
        .builderFor(create(b.baseUrl(cfg.baseUri).build()))
        .build()
        .createClient<EntraProxyClient>()

    @Bean
    fun entraProxyHealthIndicator(a: EntraProxyRestClientAdapter) =
        PingableHealthIndicator(a)
}