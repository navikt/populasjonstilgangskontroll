package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.OAuth2TokenProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraGruppeBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(builder: Builder, cfg: EntraGrupperConfig, oauth2: OAuth2TokenProvider) =
        builder.baseUrl(cfg.baseUri)
            .requestInterceptor(oauth2.interceptorFor("graph"))
            .requestInterceptor(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
            .build()
}

