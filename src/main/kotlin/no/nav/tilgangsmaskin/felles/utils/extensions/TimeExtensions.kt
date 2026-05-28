package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_0_6
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_7_12
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_OVER_24
import java.time.Clock
import java.time.Clock.systemDefaultZone
import java.time.Duration.between
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.Period.ofYears
import java.time.ZoneId
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

object TimeExtensions {

    val OSLO = ZoneId.of("Europe/Oslo")

    val ALLTID get() = LocalDate.now().plusYears(100)
    val IMORGEN get() = LocalDate.now().plusDays(1)
    val IGÅR get() = LocalDate.now().minusDays(1)

    fun Instant.isBeforeNow(clock: Clock = systemDefaultZone()) =
        isBefore(now(clock))

    fun Instant.diffFromNow(clock: Clock = systemDefaultZone()) =
        between(now(clock), this).toKotlinDuration().format()

    fun LocalDate.toInstant(zone: ZoneId = systemDefault()) =
        atStartOfDay(zone).toInstant()

    fun LocalDate.månederSidenIdag(clock: Clock = systemDefaultZone()): Int {
        val today = LocalDate.now(clock)
        require(!isAfter(today)) { "Datoen $this er etter dagens dato $today" }
        val p = Period.between(this, today)
        val rundetOpp = if (today.dayOfMonth > dayOfMonth) 1 else 0
        return (p.years * 12 + p.months + rundetOpp).coerceAtLeast(1)
    }

    fun LocalDate.intervallSiden(clock: Clock = systemDefaultZone()) =
        when (månederSidenIdag(clock)) {
            in 0..6 -> MND_0_6
            in 7..12 -> MND_7_12
            in 13..24 -> MND_13_24
            else -> MND_OVER_24
        }

    fun LocalDate.isBetween(start: LocalDate, end: LocalDate) = this in start..end

    fun Duration.format() = listOfNotNull(
        component(inWholeDays, "dag", "dager"),
        component(inWholeHours % 24, "time", "timer"),
        component(inWholeMinutes % 60, "minutt", "minutter"),
        component(inWholeSeconds % 60, "sekund", "sekunder"),
    ).joinToString(" ")

    private fun component(value: Long, singular: String, plural: String) =
        value.takeIf { it > 0 }?.let { "$it ${if (it == 1L) singular else plural}" }

    fun Long.local(fmt: String = "yyyy-MM-dd HH:mm:ss"): String =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), OSLO)
            .format(ofPattern(fmt))

    val Int.år: Period get() = ofYears(this)

    enum class Dødsperiode(val tekst: String) {
        MND_0_6("0-6"),
        MND_7_12("7-12"),
        MND_13_24("13-24"),
        MND_OVER_24(">24")
    }
}