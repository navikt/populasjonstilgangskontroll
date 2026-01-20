package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.upcase

data class RegelSett(val spec: Pair<RegelType, List<Regel>>) {
    val regler = spec.second
    val type = spec.first
    val beskrivelse = type.beskrivelse.upcase()
    val size = regler.size

    operator fun plus(other: RegelSett) = regler + other.regler

    enum class RegelType(val beskrivelse: String) {
        KJERNE_REGELTYPE(KJERNE),
        TELLENDE_REGELTYPE(TELLENDE),
        KOMPLETT_REGELTYPE(KOMPLETT),
        OVERSTYRBAR_REGELTYPE(OVERSTYRBAR)
    }

    companion object {
        const val KJERNE = "kjerneregelsett"
        const val KOMPLETT = "komplett regelsett"
        const val TELLENDE = "tellende regelsett"
        const val OVERSTYRBAR = "overstyrbart regelsett"
    }
}

enum class EvalueringType {
    BULK,
    ENKELT
}
