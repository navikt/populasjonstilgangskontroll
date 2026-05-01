package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class PdlClientBeanConfig {

    @Bean
    fun pdlClient(b: Builder, cfg: PdlConfig) =
        createClient<PdlClient>(cfg, b)

    @Bean
    fun pdlHealthIndicator(client: PdlClient, cfg: PdlConfig) =
        PingableHealthIndicator(object : AbstractPingable(cfg, client::ping) {})
}