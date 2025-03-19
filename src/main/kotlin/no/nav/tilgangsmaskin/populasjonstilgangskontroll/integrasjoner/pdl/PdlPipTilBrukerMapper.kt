package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipIdenter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipIdenter.PdlPipIdent.PdlPipIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipAdressebeskyttelse.PdlPipAdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipFamilierelasjon
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipDødsfall
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipFamilierelasjon.PdlPipFamilieRelasjonRolle
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipFamilierelasjon.PdlPipFamilieRelasjonRolle.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper.tilGeoTilknytning

object PdlPipTilBrukerMapper {

    fun tilBruker(brukerId: BrukerId, respons: PdlPipRespons, erSkjermet: Boolean) =
        with(respons) {
            Bruker(brukerId, tilGeoTilknytning(geografiskTilknytning), tilBeskyttelse(respons,erSkjermet), tilFamilie(person.familierelasjoner), erDød(person.doedsfall),tilHistoriskeBrukerIds(identer))
        }

    private fun tilBeskyttelse(respons: PdlPipRespons, erSkjermet: Boolean) =
         mutableListOf<GlobalGruppe>().apply {
            if (respons.person.adressebeskyttelse.any {
                    it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND)
                }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            } else if (respons.person.adressebeskyttelse.any { it.gradering == FORTROLIG }) {
                add(FORTROLIG_GRUPPE)
            }
            if (respons.geografiskTilknytning?.gtType == UDEFINERT || respons.geografiskTilknytning == null) {
                add(UDEFINERT_GEO_GRUPPE)
            }

            if (erSkjermet) {
                add(EGEN_ANSATT_GRUPPE)
            }
    }

    private fun erDød(dødsfall: List<PdlPipDødsfall>) = dødsfall.isNotEmpty()

    private fun tilFamilie(relasjoner: List<PdlPipFamilierelasjon>) : Familie {
        val (foreldre, barn) = relasjoner
            .mapNotNull { it.relatertPersonsIdent?.let { ident -> it.relatertPersonsRolle to ident } }.partition { it.first != BARN }
        return Familie(
            foreldre.map { FamilieMedlem(it.second, tilRelasjon(it.first)) },
            barn.map { FamilieMedlem(it.second, tilRelasjon(it.first)) })
    }

    private fun tilHistoriskeBrukerIds(identer: PdlPipIdenter) = identer.identer
        .filter { it.gruppe == FOLKEREGISTERIDENT }
        .filter { it.historisk }
        .map { (BrukerId(it.ident)) }

    private fun tilRelasjon(relasjon: PdlPipFamilieRelasjonRolle?) =
        when(relasjon) {
            MOR ->  FamilieRelasjon.MOR
            FAR ->  FamilieRelasjon.FAR
            MEDMOR ->  FamilieRelasjon.MEDMOR
            MEDFAR ->  FamilieRelasjon.MEDFAR
            BARN ->  FamilieRelasjon.BARN
            else -> throw IllegalArgumentException("Ukjent relasjon $relasjon")
        }
}