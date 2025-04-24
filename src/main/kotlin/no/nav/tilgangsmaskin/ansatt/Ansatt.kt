package no.nav.tilgangsmaskin.ansatt

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning


class Ansatt(identifikatorer: AnsattIdentifikatorer, val bruker: Bruker? = null, val grupper: Set<EntraGruppe>) {

    private val brukerId = bruker?.brukerId


    @JsonIgnore
    val ansattId = identifikatorer.ansattId

    private val foreldreEllerBarn = bruker?.foreldreOgBarn ?: emptyList()

    private val søsken = bruker?.søsken ?: emptyList()

    private val parnere = bruker?.partnere ?: emptyList()

    infix fun kanBehandle(gt: GeografiskTilknytning): Boolean {
        val kode = when (gt) {
            is KommuneTilknytning -> gt.kommune.verdi
            is BydelTilknytning -> gt.bydel.verdi
            else -> return true
        }
        return grupper.any { it.displayName.endsWith("GEO_$kode") }
    }

    infix fun erMedlemAv(gruppe: GlobalGruppe) = grupper.any { it.id == gruppe.id }

    infix fun erNåværendeEllerTidligerePartnerMed(bruker: Bruker) = bruker erEnAv parnere

    infix fun erDenSammeSom(bruker: Bruker) = brukerId == bruker.brukerId

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = bruker erEnAv foreldreEllerBarn

    infix fun erSøskenTil(bruker: Bruker) = bruker erEnAv søsken

    private infix fun Bruker.erEnAv(medlemmer: Collection<FamilieMedlem>) = medlemmer.any { it.brukerId == brukerId }

    data class AnsattIdentifikatorer(val ansattId: AnsattId, val oid: UUID)

}


