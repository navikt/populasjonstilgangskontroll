package no.nav.tilgangsmaskin.ansatt.kodeverk


import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkConfig.Companion.KODEVERK
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientRegistrationId(KODEVERK)
interface KodeverkClient {

    @GetExchange(KODEVERK_KOMMUNER_PATH)
    fun kommuner(@RequestParam(UTKAST) utkast: String = "false") : Any

    @GetExchange(KODEVERK_BYDELER_PATH)
    fun bydeler(@RequestParam(UTKAST) utkast: String = "false"): Any

    @GetExchange(KODEVERK_BYDELER_BETYDNINGER_PATH)
    fun bydelBetydninger(@RequestParam(SPRÅK) spraak: String = BOKMÅL): KodeverkBetydninger

    @GetExchange(KODEVERK_KOMMUNER_BETYDNINGER_PATH)
    fun kommuneBetydninger(@RequestParam(SPRÅK) spraak: String = BOKMÅL): KodeverkBetydninger

    @GetExchange(KODEVERK_PING_PATH)
    fun ping(): Any?

    companion object {
        private const val SPRÅK = "spraak"
        const val BOKMÅL = "nb"
        private const val UTKAST = "inkluderUtkast"
        const val KODEVERK_KOMMUNER_PATH = "/api/v1/kodeverk/Kommuner/koder"
        const val KODEVERK_BYDELER_PATH = "/api/v1/kodeverk/Bydeler/koder"
        const val KODEVERK_KOMMUNER_BETYDNINGER_PATH = "/api/v1/kodeverk/Kommuner/koder/betydninger"
        const val KODEVERK_BYDELER_BETYDNINGER_PATH = "/api/v1/kodeverk/Bydeler/koder/betydninger"
        const val KODEVERK_PING_PATH = "/internal/health/liveness"
    }
}
