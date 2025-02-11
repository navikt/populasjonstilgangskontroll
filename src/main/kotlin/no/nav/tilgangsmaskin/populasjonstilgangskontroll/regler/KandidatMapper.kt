package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.GTRespons
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person.Adressebeskyttelse.AdressebeskyttelseGradering
import org.slf4j.LoggerFactory

object KandidatMapper {
    private val log = LoggerFactory.getLogger(javaClass)
    fun mapToKandidat(fnr: Fødselsnummer, person: Person, gt: GTRespons, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(AdressebeskyttelseGradering.STRENGT_FORTROLIG,
                AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) })  add(STRENGT_FORTROLIG)
            if  (person.adressebeskyttelse.any { it.gradering in listOf(AdressebeskyttelseGradering.FORTROLIG,
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) })  add(FORTROLIG)
            if (erSkjermet) add(EGEN)
        }.toTypedArray().let {
             Kandidat(fnr, gt, *it).also { log.trace(EnvUtil.CONFIDENTIAL, "Mappet person {} til kandidat {}", person, it) }
        }
}