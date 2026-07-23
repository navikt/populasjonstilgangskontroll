package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.TexasShadowProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
@NoCoverageAnalysis
class VergemålBeanConfig {

    @Bean
    fun vergemålClient(builder: Builder, cfg: VergemålConfig, shadow: TexasShadowProvider) =
        createClient<VergemålClient>(cfg, builder, interceptors = arrayOf(shadow.interceptorFor(cfg.scope)))

    @Bean
    fun vergeHealthIndicator(client: VergemålClient, cfg: VergemålConfig) =
        PingableHealthIndicator(cfg, client::ping)
}