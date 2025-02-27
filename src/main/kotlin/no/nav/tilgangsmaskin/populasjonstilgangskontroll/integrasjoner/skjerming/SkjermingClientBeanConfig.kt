package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LocalOAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: Builder, cfg: SkjermingConfig, oAuth2ClientRequestInterceptor: LocalOAuth2ClientRequestInterceptor) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors { it.addFirst(oAuth2ClientRequestInterceptor)
            }.build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}


}

