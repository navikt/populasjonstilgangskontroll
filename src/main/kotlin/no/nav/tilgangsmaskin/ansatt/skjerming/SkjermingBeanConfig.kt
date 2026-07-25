package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@Configuration
class SkjermingBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig) =
        HttpServiceProxyFactory.builderFor(
        RestClientAdapter.create(
            builder
                .baseUrl(cfg.baseUri)
                .build()
        )
    ).build().createClient<SkjermingClient>()

    @Bean
    fun skjermingHealthIndicator(cfg: SkjermingConfig, client: SkjermingClient) =
        PingableHealthIndicator(cfg, client::ping)
}
