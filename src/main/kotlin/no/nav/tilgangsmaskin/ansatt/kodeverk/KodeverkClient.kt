package no.nav.tilgangsmaskin.ansatt.kodeverk


import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkConfig.Companion.KODEVERK
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@ClientRegistrationId(KODEVERK)
interface KodeverkClient {

    @GetExchange(KODEVERK_KOMMUNER_PATH)
    fun kommuner(@RequestParam("inkluderUtkast") spraak: String = "false") : Any

    @GetExchange(KODEVERK_BYDELER_PATH)
    fun bydeler(@RequestParam("inkluderUtkast") spraak: String = "false"): Any

    @GetExchange(KODEVERK_BYDELER_BETYDNINGER_PATH)
    fun bydelBetydninger(@RequestParam("spraak") spraak: String = "nb"): Set<KodeverkBetydningDto>

    @GetExchange(KODEVERK_KOMMUNER_BETYDNINGER_PATH)
    fun kommuneBetydninger(@RequestParam("spraak") spraak: String = "nb"): Set<KodeverkBetydningDto>

    @GetExchange(KODEVERK_PING_PATH)
    fun ping(): Any?

    companion object {
        const val KODEVERK_KOMMUNER_PATH = "/api/v1/kodeverk/Kommuner/koder"
        const val KODEVERK_BYDELER_PATH = "/api/v1/kodeverk/Bydeler/koder"
        const val KODEVERK_KOMMUNER_BETYDNINGER_PATH = "/api/v1/kodeverk/Kommuner/koder/betydninger"
        const val KODEVERK_BYDELER_BETYDNINGER_PATH = "/api/v1/kodeverk/Bydeler/koder/betydninger"
        const val KODEVERK_PING_PATH = "/internal/health/liveness"
    }
}
