package no.nav.tilgangsmaskin.security

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager

/**
 * RestClient interceptor that adds OAuth2 access tokens to requests.
 * 
 * @param authorizedClientManager The OAuth2 client manager
 * @param registrationId The OAuth2 client registration ID to use
 */
class OAuth2RestClientInterceptor(
    private val authorizedClientManager: OAuth2AuthorizedClientManager,
    private val registrationId: String
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val authorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(registrationId)
            .principal("system")
            .build()

        val authorizedClient = authorizedClientManager.authorize(authorizeRequest)

        authorizedClient?.accessToken?.let { token ->
            request.headers.setBearerAuth(token.tokenValue)
        }

        return execution.execute(request, body)
    }
}
