package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.bruker.Bruker.BrukerIds
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.erFortrolig
import no.nav.tilgangsmaskin.bruker.pdl.erStrengtFortrolig
import no.nav.tilgangsmaskin.bruker.pdl.erStrengtFortroligUtland


object PersonTilBrukerMapper {
    fun tilBruker(person: Person, erSkjermet: Boolean, oppfølgingsenhet: Enhetsnummer? = null) =
        with(person) {
            Bruker(
                    BrukerIds(brukerId, historiskeIds, aktørId),
                    geoTilknytning,
                    tilGruppeKrav(geoTilknytning, graderinger, erSkjermet),
                    familie, oppfølgingsenhet,
                dødsdato)
        }

    private fun tilGruppeKrav(gt: GeografiskTilknytning, graderinger: List<Gradering>, erSkjermet: Boolean) =
        setOfNotNull(
                when {
                    graderinger.erStrengtFortrolig() -> STRENGT_FORTROLIG
                    graderinger.erStrengtFortroligUtland() -> STRENGT_FORTROLIG_UTLAND
                    graderinger.erFortrolig() -> FORTROLIG
                    else -> null
                },
                UKJENT_BOSTED.takeIf { gt is UdefinertTilknytning },
                SKJERMING.takeIf { erSkjermet })
}