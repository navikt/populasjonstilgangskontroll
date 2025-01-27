package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractPingableHealthIndicator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LoggingGraphQLInterceptor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter.Companion.headerAddingRequestInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpSyncGraphQlClient
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.RestClient


@Configuration
class PDLClientBeanConfig {

    @Bean
    @Qualifier(PDL)
    fun pdlRestClient(b: Builder,cfg: PDLConfig) =
        b.requestInterceptors {
            it.add(headerAddingRequestInterceptor(BEHANDLINGSNUMMER) { cfg.behandlingsNummer })
        }.build()

    @Bean
    @Qualifier(PDL)
    fun syncPdlGraphQLClient(@Qualifier(PDL) client: RestClient, cfg: PDLConfig) =
        HttpSyncGraphQlClient.builder(client)
            .url(cfg.baseUri)
            .interceptor(LoggingGraphQLInterceptor())
            .build()

    @Bean
    fun pdlHealthIndicator(a: PDLGraphQLClientAdapter) = object : AbstractPingableHealthIndicator(a) {}
}