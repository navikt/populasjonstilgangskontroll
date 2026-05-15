package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator.Companion
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraOidBeanConfig {

    @Bean
    fun entraOidClient(builder: Builder, cfg: EntraGrupperConfig) =
        RestClientFactory.createClient<EntraOidClient>(
            cfg,
            builder.clone().requestInterceptors {
                it.add(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
            },
        )

    @Bean
    fun graphHealthIndicator(cfg: EntraGrupperConfig, client: EntraOidClient) =
        Companion(cfg, client::ping)
}