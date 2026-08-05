package no.nav.tilgangsmaskin.felles.rest

import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor

@Configuration
class OAuth2ClientConfig {

    @Bean
    fun authorizedClientManager(repo: ClientRegistrationRepository) =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(
            repo, InMemoryOAuth2AuthorizedClientService(repo)
        ).apply {
            setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build()
            )
        }

    @Bean
    fun oauth2RestClientCustomizer(manager: OAuth2AuthorizedClientManager) =
        RestClientCustomizer { builder ->
            builder.requestInterceptor(OAuth2ClientHttpRequestInterceptor(manager))
        }
}
