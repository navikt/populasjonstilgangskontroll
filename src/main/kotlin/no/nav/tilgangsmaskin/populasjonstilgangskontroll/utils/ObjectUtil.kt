package no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.Period
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

object ObjectUtil {
    fun requireDigits(verdi: String, len: Int): Unit {
        require(verdi.length == len){ "Ugyldig lengde ${verdi.length} for $verdi, forventet $len siffer" }
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet  $len siffer" }
    }

    fun BrukerId.mask() = verdi.mask()

    fun String.mask() = if (length == 11) replaceRange(6,11, "*****") else this


    fun  LocalDate.månederSidenIdag() =
        LocalDate.now().let {
            assert(this <= it) { "Datoen $this er etter dagens dato $it" }
            Period.between(this, it).let { it.years * 12 + it.months } + if (it.dayOfMonth > dayOfMonth) 1 else 0
        }

    private fun Duration.format(): String {
        val days = inWholeDays
        val hours = inWholeHours % 24
        val minutes = inWholeMinutes % 60
        val seconds = inWholeSeconds % 60

        return buildString {
            if (days > 0) append("$days ${if (days == 1L) "dag" else "dager"} ")
            if (hours > 0) append("$hours ${if (hours == 1L) "time" else "timer"} ")
            if (minutes > 0) append("$minutes ${if (minutes == 1L) "minutt" else "minutter"} ")
            if (seconds > 0) append("$seconds ${if (seconds == 1L) "sekund" else "sekunder"}")
        }.trim()
    }

    fun Instant.isBeforeNow() = isBefore(now())
    fun Instant.diffFromNow() = java.time.Duration.between(now(), this).toKotlinDuration().format()

}