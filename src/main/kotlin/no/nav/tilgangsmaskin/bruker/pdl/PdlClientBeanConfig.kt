package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Configuration
class PdlClientBeanConfig {

    @Bean
    fun pdlClient(b: Builder, cfg: PdlConfig, errorHandler: ErrorHandler) =
        createClient<PdlClient>(cfg, b, errorHandler)

    @Bean
    fun pdlHealthIndicator(pingable: PdlPingable) =
        PingableHealthIndicator(pingable)
}