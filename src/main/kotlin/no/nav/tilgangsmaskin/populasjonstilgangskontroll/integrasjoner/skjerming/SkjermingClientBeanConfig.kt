package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.web.client.RestClient

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: RestClient.Builder, cfg: SkjermingConfig) =
        b.baseUrl(cfg.baseUri).build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

}