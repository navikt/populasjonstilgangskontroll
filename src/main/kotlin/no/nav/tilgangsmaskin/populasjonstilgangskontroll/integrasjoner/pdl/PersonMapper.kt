package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe
import org.slf4j.LoggerFactory

object PersonMapper {

    private val log = LoggerFactory.getLogger(javaClass)
    fun mapToKandidat(fnr: Fødselsnummer,person: Person): Kandidat {
        val beskyttelse = when {
            person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) } -> GlobalGruppe.STRENGT_FORTROLIG
            person.adressebeskyttelse.any { it.gradering == FORTROLIG } -> GlobalGruppe.FORTROLIG
            else -> GlobalGruppe.INGEN
        }
        return Kandidat(fnr, beskyttelse).also { log.trace(CONFIDENTIAL, "Mapped person {} til kandidat {}", person, it) }
    }
}