package no.nav.tilgangsmaskin.bruker

data class Familie(
    val foreldre: Set<BrukerId> = emptySet(),
    val barn: Set<BrukerId> = emptySet(),
    val søsken: Set<BrukerId> = emptySet(),
    val partnere: Set<BrukerId> = emptySet(),
) {
    companion object {
        val INGEN = Familie()
    }
}