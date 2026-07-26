package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.createOAuth2Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.createClient

@Configuration
@NoCoverageAnalysis
class VergemålBeanConfig {

    @Bean
    fun vergemålClient(builder: Builder, cfg: VergemålConfig) =
        RestClientAdapter.create(builder.baseUrl(cfg.baseUri).build())
        .createOAuth2Client<VergemålClient>()

    @Bean
    fun vergeHealthIndicator(client: VergemålClient, cfg: VergemålConfig) =
        PingableHealthIndicator(cfg, client::ping)
}