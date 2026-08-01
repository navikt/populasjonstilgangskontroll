package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@NoCoverageAnalysis
class VergemålBeanConfig {

    @Bean
    fun vergeHealthIndicator(client: VergemålClient, cfg: VergemålConfig) =
        PingableHealthIndicator(cfg, client::ping)
}