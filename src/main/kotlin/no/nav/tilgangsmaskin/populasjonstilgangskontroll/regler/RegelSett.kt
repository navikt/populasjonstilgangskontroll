package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler


data class RegelSett(val type: RegelType,val regler: List<Regel>)
enum class RegelType { KJERNE, KOMPLETT }
