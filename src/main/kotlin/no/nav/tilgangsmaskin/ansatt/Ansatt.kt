package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning


data class Ansatt(val ansattId: AnsattId, val bruker: Bruker? = null, val grupper: Set<EntraGruppe>) {

    private val brukerId = bruker?.brukerId

    private val barn = bruker?.barn.orEmpty()

    private val foreldreEllerBarn = bruker?.foreldreOgBarn.orEmpty()

    private val søsken = bruker?.søsken.orEmpty()

    private val partnere = bruker?.partnere.orEmpty()

    infix fun kanBehandle(gt: GeografiskTilknytning): Boolean {
        val kode = when (gt) {
            is KommuneTilknytning -> gt.kommune.verdi
            is BydelTilknytning -> gt.bydel.verdi
            else -> return true
        }
        return harGruppeMedSuffix("GEO_$kode")
    }

    infix fun tilhører(enhet: Enhetsnummer?) =
        enhet?.let { harGruppeMedSuffix("ENHET_${it.verdi}") } ?: false

    infix fun erMedlemAv(gruppe: EntraGlobalGruppe) = grupper.any {
        it.id == gruppe.id
    }

    infix fun ikkeErMedlemAv(gruppe: EntraGlobalGruppe) = !erMedlemAv(gruppe)

    infix fun erNåværendeEllerTidligerePartnerMed(bruker: Bruker) = bruker erNærståendeMed partnere

    infix fun erDenSammeSom(bruker: Bruker) = brukerId == bruker.brukerId

    infix fun erForeldreEllerBarnTil(bruker: Bruker) = bruker erNærståendeMed foreldreEllerBarn

    infix fun erSøskenTil(bruker: Bruker) = bruker erNærståendeMed søsken

    infix fun harFellesBarnMed(bruker: Bruker) = bruker.barn.any { it in barn }

    private fun harGruppeMedSuffix(suffix: String) = grupper.any { it.displayName.endsWith(suffix) }

    private infix fun Bruker.erNærståendeMed(medlemmer: Set<BrukerId>) = brukerId in medlemmer

}
