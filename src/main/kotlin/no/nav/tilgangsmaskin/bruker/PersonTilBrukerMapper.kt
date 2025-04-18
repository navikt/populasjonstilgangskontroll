package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIdentifikatorer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertGeoTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Gradering
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
                    it in listOf(Gradering.STRENGT_FORTROLIG, Gradering.STRENGT_FORTROLIG_UTLAND)
                }) {
                add(GlobalGruppe.STRENGT_FORTROLIG)
            } else if (graderinger.any { it == Gradering.FORTROLIG }) {
                add(GlobalGruppe.FORTROLIG)
            }
            if (geoTilknytning == udefinertGeoTilknytning) {
                add(GlobalGruppe.UDEFINERT_GEO)
            }
            if (erSkjermet) {
                add(GlobalGruppe.EGEN_ANSATT)
            }
        }
}