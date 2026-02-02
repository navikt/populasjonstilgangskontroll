package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.security.OAuth2RestClientInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.client.RestClient

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(b: RestClient.Builder, cfg: EntraConfig, authorizedClientManager: OAuth2AuthorizedClientManager) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(OAuth2RestClientInterceptor(authorizedClientManager, "graph"))
                it.add(headerAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()


    @Bean
    fun graphHealthIndicator(a: EntraRestClientAdapter) =  PingableHealthIndicator(a)

    companion object {
        private val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
    }
}