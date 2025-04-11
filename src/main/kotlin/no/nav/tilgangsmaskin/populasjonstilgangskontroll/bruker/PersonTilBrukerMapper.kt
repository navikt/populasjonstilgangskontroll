package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Gradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Gradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker.BrukerIdentifikatorer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person

object PersonTilBrukerMapper {
    fun tilBruker(person: Person, erSkjermet: Boolean) =
        with(person) {
            Bruker(BrukerIdentifikatorer(brukerId, aktørId,historiskeIdentifikatorer), geoTilknytning, tilGruppeKrav(geoTilknytning,graderinger,erSkjermet), familie, dødsdato)
        }

    private fun tilGruppeKrav(geoTilknytning: GeografiskTilknytning, graderinger: List<Gradering>, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if (graderinger.any {
                    it in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND)
                }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            } else if (graderinger.any { it == FORTROLIG }) {
                add(FORTROLIG_GRUPPE)
            }
            if (geoTilknytning == UdefinertGeoTilknytning) {
                add(UDEFINERT_GEO_GRUPPE)
            }
            if (erSkjermet) {
                add(EGEN_ANSATT_GRUPPE)
            }
        }
}