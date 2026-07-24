package no.nav.tilgangsmaskin.felles.rest

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager

class OAuth2TokenInterceptor(
    private val manager: OAuth2AuthorizedClientManager,
    private val registrationId: String,
) : ClientHttpRequestInterceptor {

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution) =
        execution.execute(request.also { it.headers.setBearerAuth(token()) }, body)

    private fun token() =
        requireNotNull(manager.authorize(authorizeRequest())?.accessToken?.tokenValue) {
            "Fikk ikke token for $registrationId"
        }

    private fun authorizeRequest() = OAuth2AuthorizeRequest
        .withClientRegistrationId(registrationId)
        .principal("system")
        .build()
}
