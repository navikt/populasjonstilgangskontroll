package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.*

data class Familie(val medlemmer: Set<FamilieMedlem> = emptySet()) {

    val foreldre = medlemmer.medRelasjoner(MOR, FAR)
    val barn = medlemmer.medRelasjoner(BARN)
    val søsken = medlemmer.medRelasjoner(SØSKEN)
    val partnere = medlemmer.medRelasjoner(PARTNER, TIDLIGERE_PARTNER)

    private fun Set<FamilieMedlem>.medRelasjoner(vararg relasjoner: FamilieRelasjon) : Set<FamilieMedlem> =
        filterTo( mutableSetOf()) {
            it.relasjon in relasjoner
        }

    companion object {
        val INGEN = Familie()
    }

    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon { MOR, FAR, BARN, SØSKEN, PARTNER, TIDLIGERE_PARTNER, INGEN }
    }

}