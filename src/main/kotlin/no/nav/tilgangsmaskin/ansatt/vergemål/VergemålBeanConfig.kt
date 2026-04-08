package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.Builder

@Configuration
class VergemålBeanConfig {

    @Bean
    @Qualifier(VERGEMÅL)
    fun vergemålRestClient(b: Builder, cfg: VergemålConfig) =
        b.baseUrl(cfg.baseUri).build()

    /*
    @Bean
    @Qualifier(VERGEMÅL + "ping")
    fun vergemålPingRestClient(cfg: VergemålConfig) =
        RestClient.builder().baseUrl(cfg.baseUri).build()
*/

    @Bean
    fun vergemålHealthIndicator(a: VergemålRestClientAdapter) =
        PingableHealthIndicator(a)

}