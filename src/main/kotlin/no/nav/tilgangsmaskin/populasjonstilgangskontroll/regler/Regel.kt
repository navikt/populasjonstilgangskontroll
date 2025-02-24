package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import java.net.URI
import java.util.function.BiPredicate

interface Regel: BiPredicate<Ansatt,Bruker> {
    val metadata: RegelBeskrivelse
    val erOverstyrbar get() = this !is KjerneRegel
    data class RegelBeskrivelse(val kortNavn: String,
                                val begrunnelse: AvvisningBegrunnelse) {

        val uri =  URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")

    }
    companion object    {
        const val DETAIL_MESSAGE_CODE: String = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.detail"
        const val OVERSTYRING_MESSAGE_CODE: String = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.kjerneregler"

    }
}