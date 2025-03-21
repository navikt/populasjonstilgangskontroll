package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId as AnsattFnr


class Ansatt(val identifikatorer: AnsattIdentifikatorer, val grupper: List<EntraGruppe>, val bruker: Bruker? = null) {

    @JsonIgnore
    val ansattId = identifikatorer.ansattId
    @JsonIgnore
    val familieMedlemmer = bruker?.familieMedlemmer?.map { it.brukerId } ?: emptyList()

    fun kanBehandle(id: UUID) = grupper.any { it.id == id }
    override fun toString() = "${javaClass.simpleName} [bruker=$bruker,identifikatorer=$identifikatorer,grupper=$grupper]"

    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID, val ansattFnr: AnsattFnr? = null)

}


