package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler


data class RegelSett(val spec: Pair<RegelType, List<Regel>>) {
    val regler = spec.second
    val type = spec.first
    val tekst = type.tekst
    val size = regler.size
}
enum class RegelType(val tekst: String) { KJERNE("kjerneregelsett"), KOMPLETT("komplett regelsett") }
