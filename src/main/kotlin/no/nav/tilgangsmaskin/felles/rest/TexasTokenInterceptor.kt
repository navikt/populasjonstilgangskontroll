package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpRequest
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class TexasTokenInterceptor(private val texasClient: RestClient, private val scope: String) :
    ClientHttpRequestInterceptor {

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution) =
        execution.execute(request.also { it.headers.setBearerAuth(fetchToken()) }, body)

    private fun fetchToken(): String =
        texasClient.post()
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply {
                add("identity_provider", "azuread")
                add("target", scope)
            })
            .retrieve()
            .body<TokenResponse>()!!
            .access_token

    private data class TokenResponse(val access_token: String)
}
