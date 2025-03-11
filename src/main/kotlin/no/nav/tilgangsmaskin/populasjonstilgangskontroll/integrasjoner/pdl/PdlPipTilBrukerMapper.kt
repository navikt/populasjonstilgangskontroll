package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FamilieRelasjon
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipAdressebeskyttelse.PdlPipAdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipFamilierelasjon
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons.PdlPipPerson.PdlPipFamilierelasjon.PdlPipFamilieRelasjonRolle.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper.tilGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.slf4j.LoggerFactory

object PdlPipTilBrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
     fun tilBruker(brukerId: BrukerId, respons: PdlPipRespons, erSkjermet: Boolean): Bruker {
        return mutableListOf<GlobalGruppe>().apply {
            if  (respons.person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            else if (respons.person.adressebeskyttelse.any { it.gradering == FORTROLIG })   {
                add(FORTROLIG_GRUPPE)
            }
            if ( respons.geografiskTilknytning?.gtType == UDEFINERT || respons.geografiskTilknytning == null) {
                add(UDEFINERT_GEO_GRUPPE)
            }

            if (erSkjermet)  {
                add(EGEN_ANSATT_GRUPPE)
            }
        }.toTypedArray().let {

            Bruker(brukerId,
                tilGeoTilknytning(respons.geografiskTilknytning),
                tilFamilie(respons.person.familierelasjoner),
                *it).also {
                log.info(CONFIDENTIAL, "Mappet person {} til bruker {}", respons, it)
            }
        }
    }
    private fun tilFamilie(relasjoner: List<PdlPipFamilierelasjon>) : Familie {
        val (foreldre, barn) = relasjoner
            .mapNotNull { it.relatertPersonsIdent?.let { ident -> it.relatertPersonsRolle to ident } }.partition { it.first != BARN }
        return Familie(
            foreldre.map { FamilieMedlem(it.second, tilRelasjon(it.first)) },
            barn.map { FamilieMedlem(it.second, tilRelasjon(it.first)) })
    }

    private fun tilRelasjon(relasjon: PdlPipFamilierelasjon.PdlPipFamilieRelasjonRolle?) =
        when(relasjon) {
            MOR ->  FamilieRelasjon.MOR
            FAR ->  FamilieRelasjon.FAR
            MEDMOR ->  FamilieRelasjon.MEDMOR
            MEDFAR ->  FamilieRelasjon.MEDFAR
            BARN ->  FamilieRelasjon.BARN
            else -> throw IllegalArgumentException("Ukjent relasjon $relasjon")
        }
}