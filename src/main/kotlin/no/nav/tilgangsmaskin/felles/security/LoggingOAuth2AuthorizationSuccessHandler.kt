package no.nav.tilgangsmaskin.felles.security

import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService

class LoggingOAuth2AuthorizationSuccessHandler(
    private val service: OAuth2AuthorizedClientService,
    private val delegate: OAuth2AuthorizationSuccessHandler
) : OAuth2AuthorizationSuccessHandler {

    private val log = getLogger(javaClass)

    override fun onAuthorizationSuccess(
        authorizedClient: OAuth2AuthorizedClient,
        principal: Authentication,
        attributes: Map<String, Any>
    ) {
        val registrationId = authorizedClient.clientRegistration.registrationId
        val previous = service.loadAuthorizedClient<OAuth2AuthorizedClient>(registrationId, principal.name)
        val prevToken = previous?.accessToken?.tokenValue
        val newToken = authorizedClient.accessToken.tokenValue
        val prevExp = previous?.accessToken?.expiresAt
        val newExp = authorizedClient.accessToken.expiresAt

        when {
            previous == null ->
                log.info("OAuth2 first authorization: clientRegistrationId={}, expiresAt={}", registrationId, newExp)
            prevToken != newToken || prevExp != newExp ->
                log.info("OAuth2 token renewed: clientRegistrationId={}, oldExpiresAt={}, newExpiresAt={}", registrationId, prevExp, newExp)
            else ->
                log.debug("OAuth2 authorization success without token change: clientRegistrationId={}", registrationId)
        }

        delegate.onAuthorizationSuccess(authorizedClient, principal, attributes) // saves client
    }
}
