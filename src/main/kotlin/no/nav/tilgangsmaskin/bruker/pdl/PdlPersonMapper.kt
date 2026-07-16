package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.INGEN
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.PARTNER
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.TIDLIGERE_PARTNER
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Bydel
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.ENKE_ELLER_ENKEMANN
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GIFT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.GJENLEVENDE_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.REGISTRERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SEPARERT_PARTNER
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT
import no.nav.tilgangsmaskin.bruker.pdl.Partnere.Sivilstand.Sivilstandstype.SKILT_PARTNER
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
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.BARN
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.FAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MEDFAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MEDMOR
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MOR
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

    fun tilGeoTilknytning(geo: PdlGeografiskTilknytning?): GeografiskTilknytning =
        when (geo?.gtType) {
            UTLAND -> geo.gtLand?.let {
                UtenlandskTilknytning()
            } ?: UkjentBosted()

            KOMMUNE -> geo.gtKommune?.let {
                runCatching { KommuneTilknytning(Kommune(it.verdi)) }
                    .getOrElse {
                        log.warn("Kommunal tilknytning med ugyldig kommunekode, antar ukjent bosted")
                        UkjentBosted()
                    }
            } ?: UkjentBosted().also {
                log.warn("Kommunal tilknytning uten kommunekode, antar ukjent bosted")
            }

            BYDEL -> geo.gtBydel?.let {
                runCatching { BydelTilknytning(Bydel(it.verdi)) }
                    .getOrElse {
                        log.warn("Bydelstilknytning med ugyldig bydelskode, antar ukjent bosted")
                        UkjentBosted()
                    }
            } ?: UkjentBosted().also {
                log.warn("Bydelstilknytning uten bydelskode, antar ukjent bosted")
            }
            else -> {
                UdefinertTilknytning()
            }
        }

    private fun tilDødsdato(dødsfall: List<PdlDødsfall>) =
        dødsfall.mapNotNull { it.doedsdato }.maxOrNull()

    private fun tilFamilie(relasjoner: List<PdlFamilierelasjon>) =
        Familie(relasjoner
            .mapNotNullTo(mutableSetOf()) {
                it.relatertPersonsIdent?.let { ident ->
                    FamilieMedlem(ident, tilRelasjon(it.relatertPersonsRolle))
                }
            })


    private fun tilHistoriskeBrukerIds(identer: PdlIdenter) = identer.identer
        .filter { it.historisk }
        .filter { it.gruppe in listOf(FOLKEREGISTERIDENT, NPID) }
        .mapTo(mutableSetOf()) { (BrukerId(it.ident)) }

    private fun tilRelasjon(relasjon: PdlFamilieRelasjonRolle?) =
        when (relasjon) {
            MOR, MEDMOR -> FamilieRelasjon.MOR
            FAR, MEDFAR -> FamilieRelasjon.FAR
            BARN -> FamilieRelasjon.BARN
            else -> error("Ukjent relasjon $relasjon")
        }
}