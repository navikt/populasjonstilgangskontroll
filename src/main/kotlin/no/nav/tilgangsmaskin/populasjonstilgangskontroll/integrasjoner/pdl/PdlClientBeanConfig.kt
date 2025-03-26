package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class PdlClientBeanConfig {

    @Bean
    @Qualifier(PDL)
    fun pdlRestClient(b: Builder, oAuth2ClientRequestInterceptor: OAuth2ClientRequestInterceptor) =
        b.requestInterceptors {
            it.addFirst(oAuth2ClientRequestInterceptor)
        }.build()

    @Bean
    fun pdlHealthIndicator(a: PdlRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}
}