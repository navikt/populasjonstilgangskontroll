package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties

import org.springframework.stereotype.Component


@Component
class TokenUtil(

    private val clientConfigurationProperties: ClientConfigurationProperties,
    private val oAuth2AccessTokenService: OAuth2AccessTokenService,
) {

    fun getAppAccessTokenWithPdlScope(): String {
        val clientProperties = clientConfigurationProperties.registration["pdl-maskintilmaskin"]!!
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        return response.access_token!!
    }



}