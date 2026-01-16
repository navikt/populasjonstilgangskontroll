package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_7_12
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

object TimeExtensions {

    val ALLTID = LocalDate.now().plusYears(100)
    val IMORGEN get() = LocalDate.now().plusDays(1)
    val IGÅR get() = LocalDate.now().minusDays(1)

    fun Instant.isBeforeNow() = isBefore(now())
    fun Instant.diffFromNow() = java.time.Duration.between(now(), this).toKotlinDuration().format()
    fun LocalDate.toInstant(): Instant = atStartOfDay(systemDefault()).toInstant()

     fun LocalDate.månederSidenIdag() =
        LocalDate.now().let {
            assert(isBefore(it)) { "Datoen $this er ikke før dagens dato $it" }
            Period.between(this, it).let { it.years * 12 + it.months } + if (it.dayOfMonth > dayOfMonth) 1 else 0
        }

    fun java.time.Duration.format() = this.toKotlinDuration().format()

     fun Duration.format(): String {
        val dager = inWholeDays
        val timer = inWholeHours % 24
        val minutter = inWholeMinutes % 60
        val sekunder = inWholeSeconds % 60

        return buildString {
            if (dager > 0) append("$dager ${pluralize(dager, "dag")} ")
            if (timer > 0) append("$timer ${pluralize(timer, "time","timer")}")
            if (minutter > 0) append("$minutter ${pluralize(minutter, "minutt")}  ")
            if (sekunder > 0) append("$sekunder${pluralize(sekunder, "sekund")} ")
        }.trim()
    }

    private fun pluralize(value: Long, singular: String,plural : String = singular + "er"): String =
        if (value == 1L) singular else plural

    fun Long.local(fmt: String = "yyyy-MM-dd HH:mm:ss") = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneId.of("Europe/Oslo")
                                                                                 )
        .format(DateTimeFormatter.ofPattern(fmt))

    fun LocalDate.intervallSiden() =
        when (månederSidenIdag()) {
            in 0..6 -> MND_0_6
            in 7..12 -> MND_7_12
            in 13..24 -> MND_13_24
            else -> MND_7_12
        }

    enum class Dødsperiode(val tekst: String) {
        MND_0_6("0-6"),
        MND_7_12("7-12"),
        MND_13_24("13-24"),
        MND_OVER_24(">24")
    }
}