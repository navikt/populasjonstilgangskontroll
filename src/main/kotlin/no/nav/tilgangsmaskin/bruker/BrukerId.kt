package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tilgangsmaskin.felles.cache.JsonCacheable
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits

data class Identifikator(@JsonValue val verdi: String) {
    init {
        require(runCatching {
            AktørId(verdi)
        }.isSuccess || runCatching {
            BrukerId(verdi)
        }.isSuccess)
    }

    override fun toString() = verdi.maskFnr()
}

@JsonCacheable
data class BrukerId(@JsonValue val verdi: String) {
    init {
        with(verdi) {
            requireDigits(this, BRUKERID_LENGTH)
            if (isProd || verdi.endsWith("9096")) {
                require(mod11(this, W1) == this[9] - '0') { "Første kontrollsiffer  ${this[9]} ikke validert" }
                require(mod11(this, W2) == this[10] - '0') { "Andre kontrollsiffer  ${this[10]} ikke validert" }
            }
        }
    }

    companion object {

        const val BRUKERID_LENGTH = 11
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

data class Enhetsnummer(@JsonValue val verdi: String) {
    init {
        requireDigits(verdi, 4)
    }
}