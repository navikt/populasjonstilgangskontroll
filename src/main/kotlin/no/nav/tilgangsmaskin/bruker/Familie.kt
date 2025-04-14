package no.nav.tilgangsmaskin.bruker

data class Familie(val foreldre: Set<FamilieMedlem> = emptySet(), val barn: Set<FamilieMedlem> = emptySet(),val søsken: Set<FamilieMedlem> = emptySet(), val partnere: Set<FamilieMedlem> = emptySet()) {

    companion object {
        val INGEN = Familie()
    }
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon  {MOR, FAR, MEDMOR, MEDFAR, BARN, SØSKEN, PARTNER}
    }
}