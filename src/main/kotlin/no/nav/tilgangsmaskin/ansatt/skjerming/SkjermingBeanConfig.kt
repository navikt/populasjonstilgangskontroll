package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.createOAuth2Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter

@Configuration
class SkjermingBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig) =
        RestClientAdapter.create(builder.baseUrl(cfg.baseUri).build())
            .createOAuth2Client<SkjermingClient>()

    @Bean
    fun skjermingHealthIndicator(cfg: SkjermingConfig, client: SkjermingClient) =
        PingableHealthIndicator(cfg, client::ping)
}
