package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.felles.rest.cache.JsonCacheable

@JsonCacheable
data class Familie(
        val foreldre: Set<FamilieMedlem> = emptySet(),
        val barn: Set<FamilieMedlem> = emptySet(),
        val søsken: Set<FamilieMedlem> = emptySet(),
        val partnere: Set<FamilieMedlem> = emptySet()) {

    companion object {
        val INGEN = Familie()
    }

    @JsonCacheable
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon { MOR, FAR, BARN, SØSKEN, PARTNER, TIDLIGERE_PARTNER, INGEN }
    }
}