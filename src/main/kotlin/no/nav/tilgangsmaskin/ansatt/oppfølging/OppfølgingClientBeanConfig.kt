package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class OppfølgingClientBeanConfig {

    @Bean
    @Qualifier(OPPFØLGING)
    fun oppfølgingRestClient(b: Builder, cfg: OppfølgingConfig) = b.baseUrl(cfg.baseUri).build()

    @Bean
    fun oppfølgingHealthIndicator(a: OppfølgingRestClientAdapter) = PingableHealthIndicator(a)

}

