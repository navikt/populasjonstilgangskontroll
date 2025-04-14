package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.AvvisningKode
import java.net.URI

data class RegelBeskrivelse(val kortNavn: String, val kode: AvvisningKode) {
    companion object {
        val TYPE_URI = URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")
        const val DETAIL_MESSAGE_CODE: String =
            "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.detail"
        const val OVERSTYRING_MESSAGE_CODE: String =
            "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.kjerneregler"
    }
}