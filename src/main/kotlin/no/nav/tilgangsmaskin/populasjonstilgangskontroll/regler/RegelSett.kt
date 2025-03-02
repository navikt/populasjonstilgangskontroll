package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler


data class RegelSett(val spec: Pair<RegelType, List<Regel>>) {
    val regler = spec.second
    val type = spec.first
}
enum class RegelType(val tekst: String) { KJERNE("kjerneregler"), KOMPLETT("komplette regler") }
