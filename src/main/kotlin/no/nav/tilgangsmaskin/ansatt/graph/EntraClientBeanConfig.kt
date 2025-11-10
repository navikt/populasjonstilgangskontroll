package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraProxyConfig.Companion.PROXY
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun prodGraphRestClient(b: Builder, cfg: EntraConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(headerAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()


    @Bean
    @Qualifier(PROXY)
    @ConditionalOnNotProd
    fun devGraphRestClient(b: Builder, cfg: EntraProxyConfig) =
        b.baseUrl(cfg.baseUri).build()


    @Bean
    fun graphHealthIndicator(a: EntraRestClientAdapter) =  PingableHealthIndicator(a)

    companion object {
        private val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
    }
}