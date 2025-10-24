package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.bruker.AktørId.Companion.AKTØRID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import org.slf4j.MDC


object DomainExtensions {
    fun requireDigits(verdi: String, len: Int) {
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet $len siffer" }
        require(verdi.length == len) { "Ugyldig lengde ${verdi.length} for $verdi, forventet $len siffer" }
    }

    fun String.upcase() = this.replaceFirstChar { it.uppercaseChar() }
    fun String.maskFnr() =
        when (length) {
            BRUKERID_LENGTH -> replaceRange(6, BRUKERID_LENGTH, "*****")
            AKTØRID_LENGTH -> replaceRange(6, AKTØRID_LENGTH, "*******")
            else -> this
        }

    inline fun <T> withMDC(key: String, value: String, block: () -> T) =
        try {
            MDC.put(key, value)
            block()
        } finally {
            MDC.remove(key)
        }
}