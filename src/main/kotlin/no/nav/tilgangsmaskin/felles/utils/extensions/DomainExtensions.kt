package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.sikkerhetstjenesten.felles.utils.extensions.TimeExtensions.månederSidenIdag
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.AktørId.Companion.AKTØRID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import org.slf4j.MDC
import java.time.Clock
import java.time.LocalDate


object DomainExtensions {
    fun requireDigits(verdi: String, len: Int) {
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet $len siffer" }
        require(verdi.length == len) { "Ugyldig lengde ${verdi.length} for $verdi, forventet $len siffer" }
    }

    fun <T> withAnsattContext(ansattId: AnsattId, block: () -> T): T =
        withMDC(USER_ID to ansattId.verdi, block = block)

    fun String.upcase() = this.replaceFirstChar { it.uppercaseChar() }
    fun String.maskFnr() =
        when (length) {
            BRUKERID_LENGTH -> replaceRange(4, BRUKERID_LENGTH, "*******")
            AKTØRID_LENGTH -> replaceRange(6, AKTØRID_LENGTH, "*******")
            else -> this
        }

    inline fun <T> withMDC(vararg pairs: Pair<String, String>, block: () -> T) =
        withMDC(verdier = pairs.toMap(), block = block)

    inline fun <T> withMDC(verdier: Map<String, String>, block: () -> T) =
        try {
            verdier.forEach { (key, value) ->
                MDC.put(key, value)
            }
            block()
        } finally {
            verdier.forEach { (key, _) ->
                MDC.remove(key)
            }
        }


    enum class Dødsperiode {
        MND_0_6,
        MND_7_12,
        MND_13_24,
        MND_OVER_24
    }
    fun LocalDate.intervallSiden(clock: Clock) =
        when (månederSidenIdag(clock)) {
            in 0..6 -> Dødsperiode.MND_0_6
            in 7..12 -> Dødsperiode.MND_7_12
            in 13..24 -> Dødsperiode.MND_13_24
            else -> Dødsperiode.MND_OVER_24
        }
    const val UTILGJENGELIG = "N/A"
}