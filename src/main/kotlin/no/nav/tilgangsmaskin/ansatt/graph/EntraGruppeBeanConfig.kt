package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.TexasTokenProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraGruppeBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(builder: Builder, cfg: EntraGrupperConfig, texas: TexasTokenProvider,
                        @Value("\${texas.scope.graph}") scope: String) =
        builder.baseUrl(cfg.baseUri)
            .requestInterceptor(texas.interceptorFor(scope))
            .requestInterceptor(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
            .build()
}

