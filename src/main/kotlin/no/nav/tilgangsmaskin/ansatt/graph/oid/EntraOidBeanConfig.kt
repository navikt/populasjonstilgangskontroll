package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.health.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer

@Configuration
class EntraOidBeanConfig {

    @Bean
    fun entraGraphGroupConfigurer() = RestClientHttpServiceGroupConfigurer { groups ->
        groups.filterByName(GRAPH).forEachClient { _, builder ->
            builder.requestInterceptor(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
        }
    }

    @Bean
    fun graphHealthIndicator(cfg: EntraGrupperConfig, client: EntraOidClient) =
        PingableHealthIndicator(cfg, client::ping)
}