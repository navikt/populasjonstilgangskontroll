package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.TexasShadowProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig, shadow: TexasShadowProvider) =
        createClient<SkjermingClient>(cfg, builder, interceptors = arrayOf(shadow.interceptorFor(cfg.scope)))

    @Bean
    fun skjermingHealthIndicator(cfg: SkjermingConfig, client: SkjermingClient) =
        PingableHealthIndicator(cfg, client::ping)
}
