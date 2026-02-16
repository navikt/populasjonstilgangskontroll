package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.rest.PingableHealthIndicator
import no.nav.tilgangsmaskin.felles.security.OAuth2ClientConfig.Companion.registrationIdInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient.Builder

@Configuration
class SkjermingClientBeanConfig {

    @Bean
    @Qualifier(SKJERMING)
    fun skjermingRestClient(b: Builder, cfg: SkjermingConfig) =
        b.baseUrl(cfg.baseUri)
            .requestInterceptor(registrationIdInterceptor(SKJERMING))
            .build()

    @Bean
    fun skjermingHealthIndicator(a: SkjermingRestClientAdapter) = PingableHealthIndicator(a)

}

