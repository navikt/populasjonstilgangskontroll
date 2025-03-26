package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId as AnsattFnr


data class Ansatt(val identifikatorer: AnsattIdentifikatorer, val grupper: List<EntraGruppe>, val bruker: Bruker? = null) {

    @JsonIgnore
    val brukerId = bruker?.brukerId
    @JsonIgnore
    val ansattId = identifikatorer.ansattId
    @JsonIgnore
    val familieMedlemmer = bruker?.familieMedlemmer?.map { it.brukerId } ?: emptyList()

    @JsonIgnore
    val foreldreOgBarn = bruker?.foreldreOgBarn?.map { it.brukerId } ?: emptyList()
    @JsonIgnore
    val søsken = bruker?.søsken?.map { it.brukerId } ?: emptyList()

    fun kanBehandle(id: UUID) = grupper.any { it.id == id }
    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID, val ansattFnr: AnsattFnr? = null)

}


