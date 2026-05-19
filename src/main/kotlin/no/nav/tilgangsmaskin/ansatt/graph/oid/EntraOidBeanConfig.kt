package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.CONSISTENCY_LEVEL
import no.nav.tilgangsmaskin.felles.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.rest.RestClientFactory.createClient
import no.nav.tilgangsmaskin.felles.rest.RestHeaderAddingRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class EntraOidBeanConfig {

    @Bean
    fun entraOidClient(builder: Builder, cfg: EntraGrupperConfig) =
        createClient<EntraOidClient>(cfg,
            builder,
            interceptors = arrayOf(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL)))

    @Bean
    fun graphHealthIndicator(cfg: EntraGrupperConfig, client: EntraOidClient) =
        PingableHealthIndicator(cfg, client::ping)
}