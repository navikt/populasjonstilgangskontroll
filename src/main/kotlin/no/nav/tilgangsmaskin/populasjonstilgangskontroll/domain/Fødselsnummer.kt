package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster

@JvmInline
value class Fødselsnummer(@get:JsonValue val verdi: String) {

    enum class Type { DNR, FNR, TENOR }

    fun type() = when (verdi[0]) {
        '8', '9' -> Type.TENOR
        '4', '5' -> Type.DNR
        else -> Type.FNR
    }

    init {
        require(verdi.length == 11) { "Fødselsnummer $verdi er ikke 11 siffer" }
        if (Cluster.Companion.isProd) {
            require(mod11(W1, verdi) == verdi[9] - '0') { "Første kontrollsiffer $verdi[9] ikke validert" }
            require(mod11(W2, verdi) == verdi[10] - '0') { "Andre kontrollsiffer $verdi[10] ikke validert" }
        }
    }

    companion object {

        private fun String.partialMask(mask: Char = '*'): String {
            val start = length.div(2)
            return replaceRange(start + 1, length, mask.toString().repeat(length - start - 1))
        }

        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(weights: IntArray, fnr: String) =
            with(weights.indices.sumOf { weights[it] * (fnr[(weights.size - 1 - it)] - '0') } % 11) {
                when (this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(fnr)
                    else -> 11 - this
                }
            }
    }

    override fun toString() = "${javaClass.simpleName} [fnr=${verdi.partialMask()}]"

}