package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyConfig.Companion.ENTRAPROXY
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.ansatt.kodeverk.kodeverkConfig.Companion.KODEVERK
import no.nav.tilgangsmaskin.felles.rest.RestDefaultErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.GetExchange

interface KodeverkClient {

    @GetExchange(KODEVERK_KOMMUNER_PATH)
    @ClientRegistrationId(KODEVERK)
    fun kommuner() : Any

    @GetExchange(KODEVERK_BYDELER_PATH)
    @ClientRegistrationId(KODEVERK)
    fun bydeler(): Any

    @GetExchange(KODEVERK_PING_PATH)
    fun ping(): Any?

    companion object {
        const val KODEVERK_BYDELER_PATH = "/kodeverk/Bydeler?spraak=nb&periode=GYLDIG&status=ALLE"
        const val KODEVERK_KOMMUNER_PATH = "kodeverk/Kommuner?spraak=nb&periode=GYLDIG&status=ALLE"
        const val KODEVERK_PING_PATH = "/internal/health/liveness"
    }
}
