package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.EGEN_ANSATT
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UDEFINERT_GEO
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIdentifikatorer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertGeoTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Gradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Gradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.Person

object PersonTilBrukerMapper {
    fun tilBruker(person: Person, erSkjermet: Boolean) =
        with(person) {
            Bruker(
                BrukerIdentifikatorer(brukerId, aktørId, historiskeIdentifikatorer),
                geoTilknytning,
                tilGruppeKrav(geoTilknytning, graderinger, erSkjermet),
                familie,
                dødsdato
            )
        }

    private fun tilGruppeKrav(
        geoTilknytning: GeografiskTilknytning,
        graderinger: List<Gradering>,
        erSkjermet: Boolean
    ) =
        mutableListOf<GlobalGruppe>().apply {
            if (graderinger.any {
                    it in listOf(no.nav.tilgangsmaskin.bruker.pdl.STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND)
                }) {
                add(no.nav.tilgangsmaskin.ansatt.STRENGT_FORTROLIG)
            } else if (graderinger.any { it == no.nav.tilgangsmaskin.bruker.pdl.FORTROLIG }) {
                add(no.nav.tilgangsmaskin.ansatt.FORTROLIG)
            }
            if (geoTilknytning == udefinertGeoTilknytning) {
                add(UDEFINERT_GEO)
            }
            if (erSkjermet) {
                add(EGEN_ANSATT)
            }
        }
}