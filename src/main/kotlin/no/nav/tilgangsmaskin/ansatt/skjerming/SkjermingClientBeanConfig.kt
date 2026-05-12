package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig) =
        createClient<SkjermingClient>(cfg, builder)

    @Bean
    fun skjermingHealthIndicator(client: SkjermingClient, cfg: SkjermingConfig) =
        PingableHealthIndicator(cfg, client::ping)
}
