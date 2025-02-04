package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.FortroligeGrupper

object PersonMapper {
    fun mapToKandidat(fnr: Fødselsnummer,person: Person): Kandidat {
        val beskyttelse = when {
            person.adressebeskyttelse.any { it.gradering == STRENGT_FORTROLIG  }-> FortroligeGrupper.STRENGT_FORTROLIG
            person.adressebeskyttelse.any { it.gradering == STRENGT_FORTROLIG_UTLAND  }-> FortroligeGrupper.STRENGT_FORTROLIG_UTLAND
            person.adressebeskyttelse.any { it.gradering == FORTROLIG } -> FortroligeGrupper.FORTROLIG
            else -> null
        }
        return Kandidat(fnr, beskyttelse)
    }
}