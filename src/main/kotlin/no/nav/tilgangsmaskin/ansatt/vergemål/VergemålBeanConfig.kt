package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Configuration
class VergemålBeanConfig {

    @Bean
    fun vergemålClient(b: Builder, cfg: VergemålConfig) =
        createClient<VergemålClient>(cfg, b)

    @Bean
    fun vergemålHealthIndicator(pingable: VergemålPingable) =
        PingableHealthIndicator(pingable)
}