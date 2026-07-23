package no.nav.tilgangsmaskin.felles

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OAuth2ClientConfig {

    @Bean
    fun authorizedClientManager(repo: ClientRegistrationRepository) =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, InMemoryOAuth2AuthorizedClientService(repo)
        ).also {
            it.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build()
            )
        }
}
