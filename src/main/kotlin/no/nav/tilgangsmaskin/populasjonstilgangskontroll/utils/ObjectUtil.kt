package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import kotlin.time.Duration

object ObjectUtil {
    fun requires(verdi: String,len: Int): Unit {
        require(verdi.length == len){ "Ugyldig lengde ${verdi.length} for $verdi, forventet $len" }
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet kun $len tall" }
    }

    fun Fødselsnummer.mask() = verdi.replaceRange(6,11, "*****")

    fun Duration.format(): String {
        val days = inWholeDays
        val hours = inWholeHours % 24
        val minutes = inWholeMinutes % 60
        val seconds = inWholeSeconds % 60

        return buildString {
            if (days > 0) append("${days} dager ")
            if (hours > 0) append("${hours} timer ")
            if (minutes > 0) append("${minutes} minutter ")
            if (seconds > 0) append("${seconds} sekunder")
        }.trim()
    }
}