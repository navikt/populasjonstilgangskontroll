package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.requireDigits

@JvmInline
value class BrukerId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            requireDigits(this, 11)
            if (isProd) {
                require(mod11(this, W1) == this[9] - '0') { "Første kontrollsiffer  ${this[9]} ikke validert" }
                require(mod11(this, W2) == this[10] - '0') { "Andre kontrollsiffer  ${this[10]} ikke validert" }
            }
        }
    }

    companion object {

        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(brukerId: String, weights: IntArray) =
            with(weights.indices.sumOf { weights[it] * (brukerId[(weights.size - 1 - it)] - '0') } % 11) {
                when (this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(brukerId)
                    else -> 11 - this
                }
            }
    }

    override fun toString() = verdi.maskFnr()
    
}