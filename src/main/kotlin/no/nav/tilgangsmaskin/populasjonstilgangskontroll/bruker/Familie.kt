package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonIgnore

data class Familie(val foreldre: Set<FamilieMedlem> = emptySet(), val barn: Set<FamilieMedlem> = emptySet(),val søsken: Set<FamilieMedlem> = emptySet()) {
   @JsonIgnore
   val familieMedlemmer = foreldre + barn + søsken
    companion object {
        val INGEN = Familie()
    }
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon  {MOR,FAR,MEDMOR,MEDFAR,BARN, SØSKEN}
    }
}