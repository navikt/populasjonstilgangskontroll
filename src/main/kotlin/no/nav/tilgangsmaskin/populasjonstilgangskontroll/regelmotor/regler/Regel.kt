package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import java.net.URI
import java.util.function.BiPredicate

interface Regel: BiPredicate<Ansatt, Bruker> {
    val metadata: RegelBeskrivelse
    val avvisningTekst get() = metadata.avvisningKode.årsak
    val avvisningKode get() = metadata.avvisningKode
    val kortNavn get() = metadata.kortNavn
    val erOverstyrbar get() = this is OverstyrbarRegel
    data class RegelBeskrivelse(val kortNavn: String, val avvisningKode: AvvisningKode)
    companion object    {
        val TYPE_URI =  URI.create("https://confluence.adeo.no/display/TM/Tilgangsmaskin+API+og+regelsett")
        const val DETAIL_MESSAGE_CODE: String = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.detail"
        const val OVERSTYRING_MESSAGE_CODE: String = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException.kjerneregler"
    }
}
interface KjerneRegel : Regel
interface OverstyrbarRegel : Regel