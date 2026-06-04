package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.*

data class Familie(val medlemmer: Set<FamilieMedlem> = emptySet()) {

    val foreldre = medlemmer.filter { it.relasjon in setOf(MOR, FAR) }.toSet()
    val barn = medlemmer.filter { it.relasjon == BARN }.toSet()
    val søsken = medlemmer.filter { it.relasjon == SØSKEN }.toSet()
    val partnere = medlemmer.filter { it.relasjon in setOf(PARTNER, TIDLIGERE_PARTNER) }.toSet()

    companion object {
        val INGEN = Familie()
    }

    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon { MOR, FAR, BARN, SØSKEN, PARTNER, TIDLIGERE_PARTNER, INGEN }
    }
}

