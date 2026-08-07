package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.AktørId.Companion.AKTØRID_LENGTH
import no.nav.tilgangsmaskin.bruker.BrukerId.Companion.BRUKERID_LENGTH
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import org.slf4j.MDC


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

    const val UTILGJENGELIG = "N/A"
}