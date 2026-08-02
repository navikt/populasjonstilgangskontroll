package no.nav.tilgangsmaskin.felles.security

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

    override fun onAuthorizationSuccess(
        authorizedClient: OAuth2AuthorizedClient,
        principal: Authentication,
        attributes: Map<String, Any>
    ) {
        val downstreamUri = OAuth2DownstreamUriContext.currentUri() ?: "unknown"
        val registrationId = authorizedClient.clientRegistration.registrationId
        val previous: OAuth2AuthorizedClient? = service.loadAuthorizedClient(registrationId, principal.name)
        val prevToken = previous?.accessToken?.tokenValue
        val newToken = authorizedClient.accessToken.tokenValue
        val prevExp = previous?.accessToken?.expiresAt?.atZone(OSLO_ZONE_ID)
        val newExp = authorizedClient.accessToken.expiresAt?.atZone(OSLO_ZONE_ID)

        when {
            previous == null ->
                log.info(
                    "OAuth2 first authorization: clientRegistrationId={}, expiresAt={}, downstreamUri={}",
                    registrationId,
                    newExp,
                    downstreamUri
                )
            prevToken != newToken || prevExp != newExp ->
                log.info(
                    "OAuth2 token renewed: clientRegistrationId={}, oldExpiresAt={}, newExpiresAt={}, downstreamUri={}",
                    registrationId,
                    prevExp,
                    newExp,
                    downstreamUri
                )
            else ->
                log.debug(
                    "OAuth2 authorization success without token change: clientRegistrationId={}, downstreamUri={}",
                    registrationId,
                    downstreamUri
                )
        }

        delegate.onAuthorizationSuccess(authorizedClient, principal, attributes) // saves client
    }

    companion object {
        private val OSLO_ZONE_ID: ZoneId = ZoneId.of("Europe/Oslo")
    }
}
