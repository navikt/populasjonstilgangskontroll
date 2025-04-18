package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.EGEN_ANSATT
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UDEFINERT_GEO
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIdentifikatorer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertGeoTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.bruker.pdl.erFortrolig
import no.nav.tilgangsmaskin.bruker.pdl.erStrengtFortrolig

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
        gt: GeografiskTilknytning, graderinger: List<Gradering>, erSkjermet: Boolean
    ) =
        listOfNotNull(
            when {
                graderinger.erStrengtFortrolig() -> STRENGT_FORTROLIG
                graderinger.erFortrolig() -> FORTROLIG
                else -> null
            },
            UDEFINERT_GEO.takeIf { gt == udefinertGeoTilknytning },
            EGEN_ANSATT.takeIf { erSkjermet }
        )
}