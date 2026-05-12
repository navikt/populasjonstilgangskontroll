package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig) =
        createClient<SkjermingClient>(cfg, builder)

    @Bean
    fun skjermingHealthIndicator(cfg: SkjermingConfig, client: SkjermingClient) =
        PingableHealthIndicator(cfg, client::ping)
}
