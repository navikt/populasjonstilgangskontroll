package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestClientAdapter.Companion.behandlingRequestInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.LoggingGraphQLInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PDLConfig.Companion.PDL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@Configuration(proxyBeanMethods = false)
class PDLClientBeanConfig {

    @Bean
    @Qualifier(PDLConfig.Companion.PDL)
    fun pdlRestClient(b: RestClient.Builder, @Qualifier(PDL) clientCredentialsRequestInterceptor: ClientHttpRequestInterceptor) =
        b.requestInterceptors {
            it.addAll(listOf(clientCredentialsRequestInterceptor, behandlingRequestInterceptor()))
        }.build()

    @Bean
    @Qualifier(PDL)
    fun pdlClientCredentialsRequestInterceptor(properties: ClientConfigurationProperties, service: OAuth2AccessTokenService) =
        OAuth2ClientRequestInterceptor(properties, service)
    @Bean
    @Qualifier(PDL)
    fun syncPdlGraphQLClient(@Qualifier(PDL) client: RestClient, cfg: PDLConfig) =
        HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptor(LoggingGraphQLInterceptor())
            .build()

    @Bean
    fun pdlHealthIndicator(a: PDLGraphQLClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

    @Bean
    fun restClientCustomizer() = RestClientCustomizer {
      //it.requestFactory(HttpComponentsClientHttpRequestFactory())
    }
}