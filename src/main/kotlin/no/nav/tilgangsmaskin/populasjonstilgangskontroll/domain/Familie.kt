package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore

data class Familie(val foreldre: List<FamilieMedlem> = emptyList(), val barn: List<FamilieMedlem> = emptyList()) {
   @JsonIgnore
   val familieMedlemmer = foreldre + barn
    companion object {
        val INGEN = Familie()
    }
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon)
}