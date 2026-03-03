package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: Builder, cfg: SkjermingConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(headerAddingRequestInterceptor("X-Registration-Id" to "skjermede-personer-pip"))
            }.build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = PingableHealthIndicator(a)
}
