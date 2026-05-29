package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.UGRADERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlDødsfall
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.BARN
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import org.slf4j.LoggerFactory.getLogger

object PdlPersonMapper {
    private val log = getLogger(javaClass)

    fun tilPerson(oppslagId: String, data: PdlPipRespons) =
        with(data) {

            Person(
                BrukerId(brukerId),
                oppslagId,
                AktørId(aktørId),
                tilGeoTilknytning(geografiskTilknytning),
                tilGraderinger(person.adressebeskyttelse),
                tilFamilie(person.familierelasjoner),
                tilDødsdato(person.doedsfall),
                tilHistoriskeBrukerIds(identer)
            )
        }

    fun tilPersoner(responser: Map<String, PdlPipRespons?>): Map<String, Person> =
        responser
            .mapValues { (oppslagId, pdlRespons) -> pdlRespons?.let { tilPerson(oppslagId, it) } }
            .filterValues { it != null }
            .mapValues { it.value!! }


    private fun tilGraderinger(beskyttelse: List<PdlAdressebeskyttelse>) =
        beskyttelse.map { tilGradering(it.gradering) }

    private fun tilGradering(gradering: PdlAdressebeskyttelseGradering) =
        when (gradering) {
            STRENGT_FORTROLIG_UTLAND -> Gradering.STRENGT_FORTROLIG_UTLAND
            STRENGT_FORTROLIG -> Gradering.STRENGT_FORTROLIG
            FORTROLIG -> Gradering.FORTROLIG
            UGRADERT -> Gradering.UGRADERT
        }

    fun tilGeoTilknytning(geo: PdlGeografiskTilknytning?): GeografiskTilknytning =
        when (geo?.gtType) {
            UTLAND -> geo.gtLand?.let {
                UtenlandskTilknytning()
            } ?: UkjentBosted()

            KOMMUNE -> geo.gtKommune?.let {
                KommuneTilknytning(Kommune(it.verdi))
            } ?: UkjentBosted().also {
                log.warn("Kommunal tilknytning uten kommunekode, antar ukjent bosted")
            }

            BYDEL -> geo.gtBydel?.let {
                BydelTilknytning(Bydel(it.verdi))
            } ?: UkjentBosted().also {
                log.warn("Bydelstilknytning uten bydelskode, antar ukjent bosted")
            }

            else -> UdefinertTilknytning()
        }

    private fun tilDødsdato(dødsfall: List<PdlDødsfall>) = dødsfall.mapNotNull { it.doedsdato }.maxOrNull()

    private fun tilFamilie(relasjoner: List<PdlFamilierelasjon>): Familie {
        val (foreldre, barn) = relasjoner
            .mapNotNull { it.relatertPersonsIdent?.let { ident -> it.relatertPersonsRolle to ident } }
            .partition { it.first != BARN }
        return Familie(
            foreldre.mapTo(mutableSetOf()) { it.second },
            barn.mapTo(mutableSetOf()) { it.second })
    }

    private fun tilHistoriskeBrukerIds(identer: PdlIdenter) = identer.identer
        .filter { it.historisk }
        .filter { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }
        .map { (BrukerId(it.ident)) }.toSet()
}