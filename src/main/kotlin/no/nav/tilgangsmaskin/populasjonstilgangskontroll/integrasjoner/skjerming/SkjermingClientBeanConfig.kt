package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.FellesBeanConfig.LoggingOAuth2ClientRequestInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.web.client.RestClient

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: RestClient.Builder, cfg: SkjermingConfig, oAuth2ClientRequestInterceptor: LoggingOAuth2ClientRequestInterceptor) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors { it.add(oAuth2ClientRequestInterceptor)
            }.build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

}