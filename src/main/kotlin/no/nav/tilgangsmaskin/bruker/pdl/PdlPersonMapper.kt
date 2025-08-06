package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.AktoerId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.*
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.*
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.*
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.NPID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.*
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.BARN
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.FAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MEDFAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MEDMOR
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MOR
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import org.slf4j.LoggerFactory.getLogger

object PdlPersonMapper {
    private val log = getLogger(javaClass)

    fun tilPerson(data: PdlRespons) =
        with(data) {

            Person(
                BrukerId(brukerId),
                AktoerId(aktørId),
                tilGeoTilknytning(geografiskTilknytning),
                tilGraderinger(person.adressebeskyttelse),
                tilFamilie(person.familierelasjoner),
                tilDødsdato(person.doedsfall),
                tilHistoriskeBrukerIds(identer)
            )
        }

    fun tilPartner(type: Sivilstandstype) =
        when (type) {
            GIFT,
            REGISTRERT_PARTNER -> PARTNER
            SKILT,
            ENKE_ELLER_ENKEMANN,
            SEPARERT,
            SKILT_PARTNER,
            GJENLEVENDE_PARTNER,
            SEPARERT_PARTNER -> TIDLIGERE_PARTNER
            else -> INGEN
        }

    private fun tilGraderinger(beskyttelse: List<PdlAdressebeskyttelse>) =
        beskyttelse.map { tilGradering(it.gradering) }

    private fun tilGradering(gradering: PdlAdressebeskyttelseGradering) =
        when (gradering) {
            STRENGT_FORTROLIG_UTLAND -> Gradering.STRENGT_FORTROLIG_UTLAND
            STRENGT_FORTROLIG -> Gradering.STRENGT_FORTROLIG
            FORTROLIG -> Gradering.FORTROLIG
            UGRADERT -> Gradering.UGRADERT
        }

    private fun tilGeoTilknytning(geo: PdlGeografiskTilknytning?): GeografiskTilknytning =
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
            } ?:   UkjentBosted().also {
                log.warn("Bydelstilknytning uten bydelskode, antar ukjent bosted")
            }

            else -> UdefinertTilknytning()
        }

    private fun tilDødsdato(dødsfall: List<PdlDødsfall>) = dødsfall.maxByOrNull { it.doedsdato }?.doedsdato

    private fun tilFamilie(relasjoner: List<PdlFamilierelasjon>): Familie {
        val (foreldre, barn) = relasjoner
            .mapNotNull { it.relatertPersonsIdent?.let { ident -> it.relatertPersonsRolle to ident } }
            .partition { it.first != BARN }
        return Familie(
                foreldre.map { FamilieMedlem(it.second, tilRelasjon(it.first)) }.toSet(),
                barn.map { FamilieMedlem(it.second, tilRelasjon(it.first)) }.toSet())
    }

    private fun tilHistoriskeBrukerIds(identer: PdlIdenter) = identer.identer
        .filter { it.historisk }
        .filter { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }
        .map { (BrukerId(it.ident)) }.toSet()

    private fun tilRelasjon(relasjon: PdlFamilieRelasjonRolle?) =
        when (relasjon) {
            MOR, MEDMOR -> FamilieRelasjon.MOR
            FAR, MEDFAR -> FamilieRelasjon.FAR
            BARN -> FamilieRelasjon.BARN
            else -> error("Ukjent relasjon $relasjon")
        }
}