package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@Configuration
class EntraProxyBeanConfig {

    @Bean
    fun entraProxyClient(
        cfg: EntraProxyConfig,
        builder: Builder
    ) = HttpServiceProxyFactory.builderFor(
        RestClientAdapter.create(builder
                .baseUrl(cfg.baseUri)
                .build()
        )
    ).build().createClient<EntraProxyClient>()

    @Bean
    fun entraProxyHealthIndicator(cfg: EntraProxyConfig, client: EntraProxyClient) =
        PingableHealthIndicator(cfg, client::ping)
}