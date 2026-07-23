package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.TexasTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingBeanConfig {

    @Bean
    fun skjermingClient(builder: Builder, cfg: SkjermingConfig, texas: TexasTokenProvider,
                        @Value("\${texas.scope.skjerming}") scope: String) =
        createClient<SkjermingClient>(cfg, builder, interceptors = arrayOf(texas.interceptorFor(scope)))

    @Bean
    fun skjermingHealthIndicator(cfg: SkjermingConfig, client: SkjermingClient) =
        PingableHealthIndicator(cfg, client::ping)
}
