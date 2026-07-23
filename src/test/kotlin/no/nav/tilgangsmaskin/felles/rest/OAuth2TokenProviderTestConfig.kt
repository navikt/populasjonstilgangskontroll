package no.nav.tilgangsmaskin.felles.rest

import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.client.ClientHttpRequestInterceptor

@TestConfiguration
class OAuth2TokenProviderTestConfig {
    @Bean
    fun oauth2TokenProvider(): OAuth2TokenProvider = mockk {
        every { interceptorFor(any()) } returns ClientHttpRequestInterceptor { req, body, exec -> exec.execute(req, body) }
    }
}
