package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.createOAuth2Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.client.support.RestClientAdapter


@Configuration
class EntraGruppeBeanConfig {

    @Bean
    fun entraGrupperClient(
        builder: Builder,
        cfg: EntraGrupperConfig
    ) = RestClientAdapter.create(
        builder.baseUrl(cfg.baseUri)
            .requestInterceptor(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
            .build()
    ).createOAuth2Client<EntraGrupperClient>()
}