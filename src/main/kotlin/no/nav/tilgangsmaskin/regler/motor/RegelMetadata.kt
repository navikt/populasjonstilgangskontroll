package no.nav.tilgangsmaskin.regler.motor

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe

data class RegelMetadata(val gruppeMetadata: GruppeMetadata) {

    val kode = gruppeMetadata.kode
    val begrunnelse = gruppeMetadata.begrunnelse
    val kortNavn = gruppeMetadata.kortNavn

    constructor(gruppe: GlobalGruppe) : this(gruppe.metadata)

    companion object {
        val TYPE_URI = URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")
        const val DETAIL_MESSAGE_CODE: String =
            "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.detail"
        const val OVERSTYRING_MESSAGE_CODE: String =
            "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.kjerneregler"
    }
}