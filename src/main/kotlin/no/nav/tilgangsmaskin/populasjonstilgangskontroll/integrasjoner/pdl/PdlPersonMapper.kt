package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Companion.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.*


object PdlPersonMapper {

    fun tilPerson(data: PdlRespons) =
        with(data) {
            Person(BrukerId(data.brukerId), tilGeoTilknytning(geografiskTilknytning), tilGraderinger(person.adressebeskyttelse), tilFamilie(person.familierelasjoner), tilDødsdato(person.doedsfall),tilHistoriskeBrukerIds(identer))
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
            UTLAND ->  geo.gtLand?.let {
                UtenlandskTilknytning } ?: UkjentBosted()
            KOMMUNE -> geo.gtKommune?.let {
                KommuneTilknytning(Kommune(it.verdi))
            } ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
            BYDEL ->  geo.gtBydel?.let {
                BydelTilknytning(Bydel(it.verdi))
            }  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
            else -> UdefinertGeoTilknytning
        }

    private fun tilDødsdato(dødsfall: List<PdlDødsfall>) = dødsfall.maxByOrNull {it.doedsdato }?.doedsdato

    private fun tilFamilie(relasjoner: List<PdlFamilierelasjon>) : Familie {
        val (foreldre, barn) = relasjoner
            .mapNotNull { it.relatertPersonsIdent?.let { ident -> it.relatertPersonsRolle to ident } }.partition { it.first != BARN }
        return Familie(
            foreldre.map { FamilieMedlem(it.second, tilRelasjon(it.first)) }.toSet(),
            barn.map { FamilieMedlem(it.second, tilRelasjon(it.first)) }.toSet())
    }

    private fun tilHistoriskeBrukerIds(identer: PdlIdenter) = identer.identer
        .filter { it.historisk }
        .filter { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }
        .map { (BrukerId(it.ident)) }

    private fun tilRelasjon(relasjon: PdlFamilieRelasjonRolle?) =
        when(relasjon) {
            MOR ->  FamilieRelasjon.MOR
            FAR ->  FamilieRelasjon.FAR
            MEDMOR ->  FamilieRelasjon.MEDMOR
            MEDFAR ->  FamilieRelasjon.MEDFAR
            BARN ->  FamilieRelasjon.BARN
            else -> throw IllegalArgumentException("Ukjent relasjon $relasjon")
        }
}