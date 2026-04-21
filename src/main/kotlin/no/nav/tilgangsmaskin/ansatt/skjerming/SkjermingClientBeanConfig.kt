package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.createClient
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    fun skjermingClient(b: Builder, cfg: SkjermingConfig, errorHandler: ErrorHandler) =
        createClient<SkjermingClient>(cfg, b, errorHandler)

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = PingableHealthIndicator(a)
}
