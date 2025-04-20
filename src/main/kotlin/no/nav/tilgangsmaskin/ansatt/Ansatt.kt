package no.nav.tilgangsmaskin.ansatt

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning


data class Ansatt(
        val identifikatorer: AnsattIdentifikatorer,
        val grupper: Set<EntraGruppe>,
        val bruker: Bruker? = null) {

    private val brukerId = bruker?.brukerId

    @JsonIgnore
    val ansattId = identifikatorer.ansattId

    private val foreldreOgBarn = bruker?.foreldreOgBarn ?: emptyList()

    private val søsken = bruker?.søsken ?: emptyList()

    private val parnere = bruker?.partnere ?: emptyList()

    infix fun kanBehandle(gt: GeografiskTilknytning) = grupper.any {
        it.displayName.endsWith(
                "GEO_${
                    when (gt) {
                        is KommuneTilknytning -> gt.kommune.verdi
                        is BydelTilknytning -> gt.bydel.verdi
                        else -> return true
                    }
                }")
    }

    infix fun erMedlemAv(gruppe: GlobalGruppe) = grupper.any { it.id == gruppe.id }

    infix fun erNåværendeEllerTidligerePartnerMed(bruker: Bruker) = parnere.any { it.brukerId == bruker.brukerId }

    infix fun erSammeSom(bruker: Bruker) = brukerId == bruker.brukerId

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = foreldreOgBarn.any { it.brukerId == bruker.brukerId }

    infix fun erSøskenTil(bruker: Bruker) = søsken.any { it.brukerId == bruker.brukerId }

    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID)

}


