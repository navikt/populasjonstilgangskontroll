package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.entra.EntraConfig.Companion.HEADER_CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.FellesBeanConfig.Companion.headerAddingRequestInterceptor
import no.nav.tilgangsmaskin.felles.rest.AbstractPingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class EntraClientBeanConfig {

    @Bean
    @Qualifier(GRAPH)
    fun graphRestClient(b: RestClient.Builder, cfg: EntraConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(headerAddingRequestInterceptor(HEADER_CONSISTENCY_LEVEL))
            }.build()


    @Bean
    fun graphHealthIndicator(a: EntraRestClientAdapter) = object : AbstractPingableHealthIndicator(a) {}

}