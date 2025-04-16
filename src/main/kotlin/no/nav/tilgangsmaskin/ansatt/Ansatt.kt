package no.nav.tilgangsmaskin.ansatt

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import java.util.*


data class Ansatt(
    val identifikatorer: AnsattIdentifikatorer,
    val grupper: List<EntraGruppe>,
    val bruker: Bruker? = null
) {

    @JsonIgnore
    val brukerId = bruker?.brukerId

    @JsonIgnore
    val ansattId = identifikatorer.ansattId

    @JsonIgnore
    val foreldreOgBarn = bruker?.foreldreOgBarn ?: emptyList()

    @JsonIgnore
    val søsken = bruker?.søsken ?: emptyList()

    @JsonIgnore
    val parnere = bruker?.partnere ?: emptyList()

    infix fun harGTFor(bruker: Bruker) = grupper.any {
        it.displayName.endsWith(
            "GEO_${
                when (bruker.geografiskTilknytning) {
                    is KommuneTilknytning -> bruker.geografiskTilknytning.kommune.verdi
                    is BydelTilknytning -> bruker.geografiskTilknytning.bydel.verdi
                    else -> return true
                }
            }"
        )
    }

    infix fun kanBehandle(id: UUID) = grupper.any { it.id == id }

    infix fun erNåværendeEllerTidligerePartnerTil(bruker: Bruker) =
        parnere.any { it.brukerId == bruker.brukerId }

    infix fun er(bruker: Bruker) = brukerId == bruker.brukerId

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = foreldreOgBarn.any { it.brukerId == bruker.brukerId }

    infix fun erSøskenTil(bruker: Bruker) = søsken.any { it.brukerId == bruker.brukerId }

    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID)

}


