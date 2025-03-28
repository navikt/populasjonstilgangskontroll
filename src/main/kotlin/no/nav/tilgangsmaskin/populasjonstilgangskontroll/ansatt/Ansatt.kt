package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import java.util.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId as AnsattFnr


data class Ansatt(val identifikatorer: AnsattIdentifikatorer, val grupper: List<EntraGruppe>, val bruker: Bruker? = null) {

    @JsonIgnore
    val brukerId = bruker?.brukerId
    @JsonIgnore
    val ansattId = identifikatorer.ansattId

    @JsonIgnore
    val foreldreOgBarn = bruker?.foreldreOgBarn ?: emptyList()
    @JsonIgnore
    val søsken = bruker?.søsken ?: emptyList()

    infix fun harGTFor(bruker: Bruker) = grupper.any { it.displayName.endsWith("GEO_${
        when (bruker.geografiskTilknytning) {
            is KommuneTilknytning -> bruker.geografiskTilknytning.kommune.verdi
            is BydelTilknytning -> bruker.geografiskTilknytning.bydel.verdi
            else -> return true
        }
    }") }

    infix fun kanBehandle(id: UUID) = grupper.any { it.id == id }

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = bruker.brukerId in foreldreOgBarn.map { it.brukerId }

    infix fun erSøskenTil(bruker: Bruker) = bruker.brukerId in søsken.map { it.brukerId }

    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID, val ansattFnr: AnsattFnr? = null)

}


