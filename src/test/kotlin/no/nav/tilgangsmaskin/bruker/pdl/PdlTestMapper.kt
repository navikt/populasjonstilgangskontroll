package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UdefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.BYDEL
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UDEFINERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.UGRADERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlDødsfall
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.BARN
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.FAR
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlFamilierelasjon.PdlFamilieRelasjonRolle.MOR
import tools.jackson.databind.json.JsonMapper

object PdlTestMapper {

    fun pdlRespons(p: Person) = PdlRespons(
        PdlPerson(
            buildSet {
                p.graderinger.forEach {
                    add(PdlAdressebeskyttelse(tilPdlGradering(it)))
                }
            },
            setOfNotNull(p.dødsdato?.let {
                PdlDødsfall(it)
            }),
            buildSet {
                tilFamilierelasjoner(p.familie).values.forEach {
                    addAll(it)
                }
            }
        ),
        PdlIdenter(
            buildSet {
                add(PdlIdent(p.brukerId.verdi, false, FOLKEREGISTERIDENT))
                add(PdlIdent(p.aktørId.verdi, false, AKTORID))
                p.historiskeIds.forEach {
                    add(PdlIdent(it.verdi, true, FOLKEREGISTERIDENT))
                }
            }
        ),
        tilPdlGeografiskTilknytning(p.geoTilknytning)
    )


    private fun tilPdlGeografiskTilknytning(geo: GeografiskTilknytning) =
        when (geo) {
            is KommuneTilknytning -> PdlGeografiskTilknytning(KOMMUNE,
                gtKommune = PdlGeografiskTilknytning.GTKommune(geo.kommune.verdi))
            is BydelTilknytning      -> PdlGeografiskTilknytning(BYDEL,
                gtBydel = PdlGeografiskTilknytning.GTBydel(geo.bydel.verdi))
            is UtenlandskTilknytning -> PdlGeografiskTilknytning(UTLAND,
                gtLand = PdlGeografiskTilknytning.GTLand("NOR"))
            is UkjentBosted,
            is UdefinertTilknytning  -> PdlGeografiskTilknytning(UDEFINERT)
        }

    fun personRespons(mapper: JsonMapper, p: Person) =
        mapper.writeValueAsString(pdlRespons(p))

    fun restRespons(mapper: JsonMapper, p: Person) =
        mapper.writeValueAsString(mapOf(p.brukerId.verdi to pdlRespons(p)))

    fun tilFamilierelasjoner(familie: Familie): Map<FamilieRelasjon, List<PdlFamilierelasjon>> =
        (familie.foreldre + familie.barn + familie.partnere + familie.søsken)
            .groupBy { it.relasjon }
            .mapValues { (relasjon, medlemmer) ->
                val rolle = tilPdlRolle(relasjon)
                medlemmer.map { PdlFamilierelasjon(it.brukerId, rolle) }
            }

    private fun tilPdlRolle(relasjon: FamilieRelasjon): PdlFamilieRelasjonRolle? =
        when (relasjon) {
            FamilieRelasjon.MOR  -> MOR
            FamilieRelasjon.FAR  -> FAR
            FamilieRelasjon.BARN -> BARN
            else -> null
        }

    private fun tilPdlGradering(gradering: Gradering): PdlAdressebeskyttelseGradering =
        when (gradering) {
            Gradering.STRENGT_FORTROLIG_UTLAND -> STRENGT_FORTROLIG_UTLAND
            Gradering.STRENGT_FORTROLIG  -> STRENGT_FORTROLIG
            Gradering.FORTROLIG-> FORTROLIG
            Gradering.UGRADERT  -> UGRADERT
        }
}

object BrukerTilPersonMapper {

    fun tilPerson(bruker: Bruker) = with(bruker) {
        Person(
            brukerId      = brukerId,
            oppslagId     = oppslagId,
            aktørId       = aktørId,
            geoTilknytning = geografiskTilknytning,
            familie       = familie,
            dødsdato      = dødsdato,
            historiskeIds = historiskeIds
        )
    }
}