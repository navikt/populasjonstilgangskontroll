package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.AdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person
import org.slf4j.LoggerFactory

object KandidatMapper {

    private val log = LoggerFactory.getLogger(javaClass)
    fun mapToKandidat(fnr: Fødselsnummer, person: Person, skjermet: Boolean): Kandidat {
        val beskyttelse = mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(AdressebeskyttelseGradering.STRENGT_FORTROLIG,
                AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) })  add(GlobalGruppe.STRENGT_FORTROLIG)
            if  (person.adressebeskyttelse.any { it.gradering in listOf(AdressebeskyttelseGradering.FORTROLIG,
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) })  add(GlobalGruppe.FORTROLIG)
            if (skjermet) add(GlobalGruppe.EGEN)
        }
        return Kandidat(fnr, beskyttelse).also { log.trace(EnvUtil.CONFIDENTIAL, "Mappet person {} til kandidat {}", person, it) }
    }
}