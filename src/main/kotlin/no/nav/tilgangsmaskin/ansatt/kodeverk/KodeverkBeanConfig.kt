package no.nav.tilgangsmaskin.ansatt.kodeverk


import no.nav.tilgangsmaskin.felles.rest.health.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KodeverkBeanConfig {

    @Bean
    fun kodeverkHealthIndicator(cfg: KodeverkConfig, client: KodeverkClient) =
        PingableHealthIndicator(cfg, client::ping)
}