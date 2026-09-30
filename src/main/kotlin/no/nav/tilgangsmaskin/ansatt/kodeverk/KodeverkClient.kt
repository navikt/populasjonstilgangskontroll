package no.nav.tilgangsmaskin.ansatt.kodeverk


import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkConfig.Companion.KODEVERK
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.service.annotation.GetExchange

@ClientRegistrationId(KODEVERK)
interface KodeverkClient {

    @GetExchange(KODEVERK_KOMMUNER_PATH)
    fun kommuner() : Any

    @GetExchange(KODEVERK_BYDELER_PATH)
    fun bydeler(): Any

    @GetExchange(KODEVERK_PING_PATH)
    fun ping(): Any?

    companion object {
        const val KODEVERK_BYDELER_PATH = "/kodeverk/Bydeler?spraak=nb&periode=GYLDIG&status=ALLE"
        const val KODEVERK_KOMMUNER_PATH = "kodeverk/Kommuner?spraak=nb&periode=GYLDIG&status=ALLE"
        const val KODEVERK_PING_PATH = "/internal/health/liveness"
    }
}
