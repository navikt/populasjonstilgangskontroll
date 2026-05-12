package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(builder: Builder, cfg: EntraConfig) =
        builder.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(RestHeaderAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()


    @Bean
    fun entraGraphClient(builder: Builder, cfg: EntraConfig) =
        createClient<EntraClient>(cfg,
            builder.clone().requestInterceptors {
                it.add(RestHeaderAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            },
        )

    @Bean
    fun graphHealthIndicator(cfg: EntraConfig, client: EntraClient) =
        PingableHealthIndicator(cfg, client::ping)

     companion object {
         val HEADER_CONSISTENCY_LEVEL = "ConsistencyLevel" to "eventual"
    }
}