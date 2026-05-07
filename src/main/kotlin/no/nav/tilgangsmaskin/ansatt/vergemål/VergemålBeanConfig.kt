package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class VergemålBeanConfig {

    @Bean
    fun vergemålClient(builder: Builder, cfg: VergemålConfig) =
        createClient<VergemålClient>(cfg, builder)

    @Bean
    fun vergemålHealthIndicator(pingable: VergemålPingable) =
        PingableHealthIndicator(pingable)
}