package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.BEHANDLINGSNUMMER
import no.nav.tilgangsmaskin.bruker.pdl.PdlGraphQLConfig.Companion.PDLGRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.health.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.security.OAuth2DownstreamUriCapturingInterceptor
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpSyncGraphQlClient.builder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
@NoCoverageAnalysis
class PdlGraphBeanConfig {

    @Bean
    @Qualifier(PDLGRAPH)
    fun pdlGraphRestClient(builder: Builder, mgr: OAuth2AuthorizedClientManager, failureHandler: OAuth2AuthorizationFailureHandler) =
        builder
            .requestInterceptors {
                it.add(OAuth2DownstreamUriCapturingInterceptor())
                it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
                it.add(OAuth2ClientHttpRequestInterceptor(mgr).apply {
                    setClientRegistrationIdResolver { PDLGRAPH }
                    setAuthorizationFailureHandler(failureHandler)
                })
            }
            .build()

    @Bean
    fun pdlGraphSyncGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig) =
        builder(client)
            .url(cfg.baseUri)
            .interceptors {
                it.addFirst(PdlGraphQLLoggingInterceptor())
            }.build()

    @Bean
    fun pdlGraphHealthIndicator(cfg: PdlGraphQLConfig, @Qualifier(PDLGRAPH) client: RestClient) =
        PingableHealthIndicator(cfg) {
            client.options()
                .uri(cfg.baseUri)
                .retrieve()
                .toBodilessEntity()
        }
}
