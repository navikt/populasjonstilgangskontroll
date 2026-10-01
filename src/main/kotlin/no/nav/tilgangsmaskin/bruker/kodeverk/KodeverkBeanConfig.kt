package no.nav.tilgangsmaskin.bruker.kodeverk


import no.nav.sikkerhetstjenesten.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Configuration

@Configuration
class KodeverkBeanConfig {

   // @Bean
    fun kodeverkHealthIndicator(cfg: KodeverkConfig, client: KodeverkClient) =
       PingableHealthIndicator(cfg, client::ping)
}