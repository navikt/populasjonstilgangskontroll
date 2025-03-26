package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Gradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Gradering.*

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person

object PersonTilBrukerMapper {
    fun tilBruker(person: Person, erSkjermet: Boolean) =
        with(person) {
            Bruker(brukerId, geoTilknytning, tilGruppeKrav(geoTilknytning,graderinger,erSkjermet), familie, dødsdato,historiskeIdentifikatorer)
        }

    private fun tilGruppeKrav(geo: GeoTilknytning, graderinger: List<Gradering>, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if (graderinger.any {
                    it in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND)
                }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            } else if (graderinger.any { it == FORTROLIG }) {
                add(FORTROLIG_GRUPPE)
            }
            if (geo == UdefinertGeoTilknytning) {
                add(UDEFINERT_GEO_GRUPPE)
            }
            if (erSkjermet) {
                add(EGEN_ANSATT_GRUPPE)
            }
        }
}