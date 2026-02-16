package no.nav.tilgangsmaskin.felles.security

import com.nimbusds.jose.jwk.JWK
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OAuth2ClientConfig {

    @Value("\${azure.app.jwk:}")
    private lateinit var clientJwk: String

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository
    ): OAuth2AuthorizedClientManager {
        val tokenResponseClient = RestClientClientCredentialsTokenResponseClient()

        // Configure JWT client authentication for private_key_jwt
        if (clientJwk.isNotBlank()) {
            val jwtConverter = NimbusJwtClientAuthenticationParametersConverter<OAuth2ClientCredentialsGrantRequest> {
                JWK.parse(clientJwk)
            }
            tokenResponseClient.addParametersConverter(jwtConverter)
        }

        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials { it.accessTokenResponseClient(tokenResponseClient) }
            .build()

        val authorizedClientService = InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)

        return AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        ).apply {
            setAuthorizedClientProvider(authorizedClientProvider)
        }
    }

    @Bean
    fun oauth2ClientRequestInterceptor(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): ClientHttpRequestInterceptor = ClientHttpRequestInterceptor { request, body, execution ->
        val registrationId = request.headers.getFirst(REGISTRATION_ID_HEADER)
        if (registrationId != null) {
            request.headers.remove(REGISTRATION_ID_HEADER)
            val authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationId)
                .principal(registrationId)
                .build()
            val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
            authorizedClient?.accessToken?.tokenValue?.let { token ->
                request.headers.setBearerAuth(token)
            }
        }
        execution.execute(request, body)
    }

    companion object {
        const val REGISTRATION_ID_HEADER = "X-OAuth2-Registration-Id"

        fun registrationIdInterceptor(registrationId: String) =
            ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add(REGISTRATION_ID_HEADER, registrationId)
                execution.execute(request, body)
            }
    }
}



