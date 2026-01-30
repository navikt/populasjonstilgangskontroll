package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.security.OAuth2RestClientInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: Builder, cfg: SkjermingConfig, authorizedClientManager: OAuth2AuthorizedClientManager) = 
        b.baseUrl(cfg.baseUri)
            .requestInterceptors {
                it.add(OAuth2RestClientInterceptor(authorizedClientManager, "skjermede-personer-pip"))
            }
            .build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = PingableHealthIndicator(a)

}
