package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.sikkerhetstjenesten.felles.rest.PingableHealthIndicator
import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@NoCoverageAnalysis
class VergemålBeanConfig {

    @Bean
    fun vergeHealthIndicator(client: VergemålClient, cfg: VergemålConfig) =
        PingableHealthIndicator(cfg, client::ping)
}