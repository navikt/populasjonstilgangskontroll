package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder


@Configuration
class EntraGruppeBeanConfig {

    @Bean
    fun entraGrupperClient(builder: Builder, cfg: EntraGrupperConfig) =
        createClient<EntraGrupperClient>(cfg, builder,
            RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL)
        )
}