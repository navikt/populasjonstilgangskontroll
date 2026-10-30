package no.nav.tilgangsmaskin.ansatt.entraproxy

import no.nav.sikkerhetstjenesten.felles.notifikasjon.MailRequest
import no.nav.sikkerhetstjenesten.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface EntraProxyClient {
    @GetExchange(ENTRA_PROXY_ANSATT_PATH)
    @ClientRegistrationId(ENTRAPROXY)
    fun enhet(
        @PathVariable navIdent: String,
        @RequestHeader(IDENTIFIKATOR) identifikator: String = navIdent,
    ): EntraProxyEnhet

    @PostExchange(ENTRA_PROXY_MAIL_PATH)
    @ClientRegistrationId(ENTRAPROXY)
    fun mail(
        @RequestBody message: MailRequest,
    )

    @GetExchange(ENTRA_PROXY_PING_PATH)
    fun ping(): Any?

    companion object {
        const val ENTRA_PROXY_MAIL_PATH = "/api/v1/mail"
        const val ENTRA_PROXY_ANSATT_PATH = "/api/v1/ansatt/{navIdent}"
        const val ENTRA_PROXY_PING_PATH = "/monitoring/health/liveness"
    }
}
