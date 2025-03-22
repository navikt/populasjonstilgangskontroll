package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Cluster
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask

@JvmInline
value class BrukerId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            ObjectUtil.requireDigits(this, 11)
            if (Cluster.isProd) {
                require(mod11(W1, this) == this[9] - '0') { "Første kontrollsiffer  ${this[9]} ikke validert" }
                require(mod11(W2, this) == this[10] - '0') { "Andre kontrollsiffer  ${this[10]} ikke validert" }
            }
        }
    }

    companion object {

        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(weights: IntArray, brukerId: String) =
            with(weights.indices.sumOf { weights[it] * (brukerId[(weights.size - 1 - it)] - '0') } % 11) {
                when (this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(brukerId)
                    else -> 11 - this
                }
            }
    }

    override fun toString() = "${javaClass.simpleName} [verdi=${mask()}]"

}