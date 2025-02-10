package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

object PersonMapper {
    fun mapToKandidat(fnr: Fødselsnummer,person: Person): Kandidat {
        val beskyttelse = when {
            person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) } -> GlobalGruppe.STRENGT_FORTROLIG
            person.adressebeskyttelse.any { it.gradering == FORTROLIG } -> GlobalGruppe.FORTROLIG
            else -> null
        }
        return Kandidat(fnr, beskyttelse)
    }
}