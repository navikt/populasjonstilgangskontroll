package no.nav.tilgangsmaskin.felles.security

import org.slf4j.LoggerFactory.getLogger
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.ClientAuthorizationException
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.core.OAuth2AuthorizationException

class LoggingOAuth2AuthorizationFailureHandler(
    private val delegate: OAuth2AuthorizationFailureHandler
) : OAuth2AuthorizationFailureHandler {

    private val log = getLogger(javaClass)

    override fun onAuthorizationFailure(
        authorizationException: OAuth2AuthorizationException,
        principal: Authentication,
        attributes: Map<String, Any>
    ) {
        val registrationId = (authorizationException as? ClientAuthorizationException)?.clientRegistrationId ?: "unknown"
        log.debug(
            "OAuth2 authorization failed for clientRegistrationId={}, principalType={}, errorCode={}",
            registrationId,
            principal.javaClass.simpleName,
            authorizationException.error.errorCode
        )
        delegate.onAuthorizationFailure(authorizationException, principal, attributes)
    }
}
