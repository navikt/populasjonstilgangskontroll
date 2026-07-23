package no.nav.tilgangsmaskin.felles.rest

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TexasShadowProvider(@Value("\${texas.token-endpoint}") endpoint: String) {
    private val texasClient = RestClient.create(endpoint)

    fun interceptorFor(scope: String): ClientHttpRequestInterceptor = TexasShadowInterceptor(scope, texasClient)
}
