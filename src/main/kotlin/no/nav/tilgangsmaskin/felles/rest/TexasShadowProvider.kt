package no.nav.tilgangsmaskin.felles.rest

import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TexasShadowProvider(
    @Value("\${texas.token-endpoint}") endpoint: String,
    builder: RestClient.Builder,
) {
    private val texasClient = builder
        .baseUrl(endpoint)
        .requestInterceptors { it.removeIf { i -> i is OAuth2ClientRequestInterceptor } }
        .build()

    fun interceptorFor(scope: String): ClientHttpRequestInterceptor = TexasShadowInterceptor(scope, texasClient)
}
