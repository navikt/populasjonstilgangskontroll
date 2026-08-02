package no.nav.tilgangsmaskin.felles.security

import org.slf4j.LoggerFactory.getLogger
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient

class LoggingOAuth2AuthorizationSuccessHandler(
    private val delegate: OAuth2AuthorizationSuccessHandler
) : OAuth2AuthorizationSuccessHandler {

    private val log = getLogger(javaClass)

    override fun onAuthorizationSuccess(
        authorizedClient: OAuth2AuthorizedClient,
        principal: Authentication,
        attributes: Map<String, Any>
    ) {
        log.debug(
            "OAuth2 authorization succeeded for clientRegistrationId={}, principalType={}",
            authorizedClient.clientRegistration.registrationId,
            principal.javaClass.simpleName
        )
        delegate.onAuthorizationSuccess(authorizedClient, principal, attributes)
    }
}
