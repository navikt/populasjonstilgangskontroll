package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonIgnore

data class Familie(val foreldre: List<FamilieMedlem> = emptyList(), val barn: List<FamilieMedlem> = emptyList(),val søsken: List<FamilieMedlem> = emptyList()) {
   @JsonIgnore
   val familieMedlemmer = foreldre + barn + søsken
    companion object {
        val INGEN = Familie()
    }
    data class FamilieMedlem(val brukerId: BrukerId, val relasjon: FamilieRelasjon) {
        enum class FamilieRelasjon  {MOR,FAR,MEDMOR,MEDFAR,BARN, SØSKEN}

    }
}