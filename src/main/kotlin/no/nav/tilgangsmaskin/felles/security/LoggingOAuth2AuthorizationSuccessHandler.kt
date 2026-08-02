package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.security.OAuth2DownstreamUriContext.currentUri
import org.slf4j.LoggerFactory.getLogger
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import java.time.ZoneId

class LoggingOAuth2AuthorizationSuccessHandler(
    private val service: OAuth2AuthorizedClientService,
    private val delegate: OAuth2AuthorizationSuccessHandler
) : OAuth2AuthorizationSuccessHandler {

    private val log = getLogger(javaClass)

    override fun onAuthorizationSuccess(denne: OAuth2AuthorizedClient, principal: Authentication, attr: Map<String, Any>) {
        val uri = currentUri() ?: "unknown"
        val id = denne.clientRegistration.registrationId
        val forrige: OAuth2AuthorizedClient? = service.loadAuthorizedClient(id, principal.name)
        val prevExp = forrige?.accessToken?.expiresAt?.atZone(OSLO_ZONE_ID)
        val newExp = denne.accessToken.expiresAt?.atZone(OSLO_ZONE_ID)

        if (forrige == null) {
            log.info("OAuth2 første autorisering: id={}, expiresAt={}, uri={}", id, newExp, uri)
        } else {
            log.info("OAuth2 token fornyelse: id={}, oldExpiresAt={}, newExpiresAt={}, uri={}", id,prevExp,newExp,uri)
        }
        delegate.onAuthorizationSuccess(denne, principal, attr)
    }

    companion object {
        private val OSLO_ZONE_ID: ZoneId = ZoneId.of("Europe/Oslo")
    }
}
