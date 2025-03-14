package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

data class RegelSett(val spec: Pair<RegelType, List<Regel>>) {
    val regler = spec.second
    val type = spec.first
    val beskrivelse = type.beskrivelse
    val size = regler.size

    enum class RegelType(val beskrivelse: String) {
        KJERNE_REGELTYPE(KJERNE),
        KOMPLETT_REGELTYPE(KOMPLETT)
    }

    companion object {
        const val KJERNE = "kjerneregelsett"
        const val KOMPLETT = "komplett regelsett"
    }
}
