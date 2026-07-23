package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.stereotype.Component

@Component
class OAuth2TokenProvider(private val manager: OAuth2AuthorizedClientManager) {
    fun interceptorFor(registrationId: String): ClientHttpRequestInterceptor =
        OAuth2TokenInterceptor(manager, registrationId)
}
