package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.security.OAuth2DownstreamURIContext.currentUri
import org.slf4j.LoggerFactory.getLogger
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.ClientAuthorizationException
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler
import org.springframework.security.oauth2.core.OAuth2AuthorizationException

class OAuth2LoggingAuthorizationFailureHandler(
    private val delegate: OAuth2AuthorizationFailureHandler) : OAuth2AuthorizationFailureHandler {
    private val log = getLogger(javaClass)

    override fun onAuthorizationFailure(e: OAuth2AuthorizationException, principal: Authentication, attr: Map<String, Any>) {
        log.debug(
            "OAuth2 authorization feilet for id={}, errorCode={}, uri={}",
            (e as? ClientAuthorizationException)?.clientRegistrationId ?: "unknown",
            e.error.errorCode,
            currentUri() ?: "unknown"
        )
        delegate.onAuthorizationFailure(e, principal, attr)
    }
}
