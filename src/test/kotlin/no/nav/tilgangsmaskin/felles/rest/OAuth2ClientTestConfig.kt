package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatusCode

@TestConfiguration
class OAuth2ClientTestConfig {

    @Bean
    fun restClientCustomizer() = RestClientCustomizer { c ->
        c.defaultStatusHandler(HttpStatusCode::isError, RestDefaultErrorHandler()::handle)
    }
}
