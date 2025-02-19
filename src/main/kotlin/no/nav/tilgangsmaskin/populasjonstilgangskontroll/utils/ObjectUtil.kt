package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer

object ObjectUtil {
    fun requires(verdi: String,len: Int): Unit {
        require(verdi.length == len){ "Ugyldig lengde ${verdi.length} for $verdi, forventet $len" }
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet kun $len tall" }
    }

    fun Fødselsnummer.mask() = verdi.replaceRange(6,11, "*****")
}