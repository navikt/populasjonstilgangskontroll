package no.nav.tilgangsmaskin.security

import com.nimbusds.jose.jwk.JWK
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import java.util.function.Function

@Configuration
class OAuth2ClientConfig {

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService,
        @Value("\${azure.app.jwk}") jwkString: String
    ): OAuth2AuthorizedClientManager {
        // Parse the JWK from configuration
        val jwk = JWK.parse(jwkString)
        
        // Configure JWT client authentication converter with the JWK
        val jwtClientAuthenticationConverter = NimbusJwtClientAuthenticationParametersConverter<*>(
            Function { clientRegistration -> jwk }
        )
        
        // Create the request entity converter with JWT authentication support
        val requestEntityConverter = OAuth2ClientCredentialsGrantRequestEntityConverter()
        requestEntityConverter.addParametersConverter(jwtClientAuthenticationConverter)
        
        // Configure the token response client
        val tokenResponseClient = DefaultClientCredentialsTokenResponseClient()
        tokenResponseClient.setRequestEntityConverter(requestEntityConverter)
        
        // Build the authorized client provider with the custom token response client
        val authorizedClientProvider: OAuth2AuthorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials { it.accessTokenResponseClient(tokenResponseClient) }
            .build()

        val authorizedClientManager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }
}
