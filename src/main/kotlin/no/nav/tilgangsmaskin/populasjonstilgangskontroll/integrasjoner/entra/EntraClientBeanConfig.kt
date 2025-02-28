package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.HEADER_CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter.Companion.headerAddingRequestInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(b: RestClient.Builder, cfg: EntraConfig, oAuth2ClientRequestInterceptor: OAuth2ClientRequestInterceptor) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.addFirst(oAuth2ClientRequestInterceptor)
                it.add(headerAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()

    @Bean
    fun graphHealthIndicator(a: EntraClientAdapter) = object : AbstractPingableHealthIndicator(a) {}
}