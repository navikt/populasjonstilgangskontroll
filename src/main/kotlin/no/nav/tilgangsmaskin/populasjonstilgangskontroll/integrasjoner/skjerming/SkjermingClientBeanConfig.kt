package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestClientAdapter.Companion.behandlingRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.LoggingGraphQLInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PDLConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@Configuration(proxyBeanMethods = false)
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: RestClient.Builder, clientCredentialsRequestInterceptor:  OAuth2ClientRequestInterceptor, cfg: SkjermingConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.addAll(listOf(clientCredentialsRequestInterceptor, behandlingRequestInterceptor()))
            }.build()
    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

}