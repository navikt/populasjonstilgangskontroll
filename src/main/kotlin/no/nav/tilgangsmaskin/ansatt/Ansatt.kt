package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning


data class Ansatt(val ansattId: AnsattId, val bruker: Bruker? = null, val grupper: Set<EntraGruppe>) {

    private val brukerId = bruker?.brukerId

    private val barn = bruker?.barn ?: emptySet()

    private val foreldreEllerBarn = bruker?.foreldreOgBarn ?: emptySet()

    private val søsken = bruker?.søsken ?: emptySet()

    private val partnere = bruker?.partnere ?: emptySet()

    infix fun kanBehandle(gt: GeografiskTilknytning): Boolean {
        val kode = when (gt) {
            is KommuneTilknytning -> gt.kommune.verdi
            is BydelTilknytning -> gt.bydel.verdi
            else -> return true
        }
        return grupper.any { it.displayName.endsWith("GEO_$kode") }
    }


    infix fun erMedlemAv(gruppe: GlobalGruppe) = grupper.any { it.id == gruppe.id }

    infix fun erNåværendeEllerTidligerePartnerMed(bruker: Bruker) = bruker erNærståendeMed partnere

    infix fun erDenSammeSom(bruker: Bruker) = brukerId == bruker.brukerId

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = bruker erNærståendeMed foreldreEllerBarn

    infix fun erSøskenTil(bruker: Bruker) = bruker erNærståendeMed søsken

    infix fun harFellesBarnMed(bruker: Bruker) = bruker.barn harMinstEnFelles barn

    private infix fun Set<FamilieMedlem>.harMinstEnFelles(medlemmer: Set<FamilieMedlem>) =
        intersect(medlemmer).isNotEmpty()

    private infix fun Bruker.erNærståendeMed(medlemmer: Set<FamilieMedlem>) =
        medlemmer.any { it.brukerId == brukerId }
}


