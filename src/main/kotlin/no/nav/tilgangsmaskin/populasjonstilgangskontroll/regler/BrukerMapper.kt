package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import com.neovisionaries.i18n.CountryCode.getByAlpha3Code
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Bydel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Companion.UDEFINERT_GEO_TILKNYTNING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.GT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.GT.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Person.Adressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.slf4j.LoggerFactory

object BrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
    fun mapToBruker(fnr: Fødselsnummer, person: Person, gt: GT, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) })   {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            if  (person.adressebeskyttelse.any { it.gradering == FORTROLIG})   {
                add(FORTROLIG_GRUPPE)
            }
            if (erSkjermet)  {
                add(EGEN_GRUPPE)
            }
        }.toTypedArray().let {
             Bruker(fnr, mapNavn(person.navn),mapTilknytning(gt), *it).also { log.trace(CONFIDENTIAL, "Mappet person {} til kandidat {}", person, it) }
        }

    private fun mapNavn(navn: List<Person.Navn>): Navn {
         navn.first().let {
            return Navn(it.fornavn, it.etternavn, it.mellomnavn)
        }
    }

    private fun mapTilknytning(respons: GT): GEOTilknytning = when (respons.gtType) {
        UTLAND ->  respons.gtLand?.let {  UtenlandskTilknytning(getByAlpha3Code(it.verdi)) } ?: throw IllegalStateException("Utenlandsk tilknytning uten landkode")
        KOMMUNE -> respons.gtKommune?.let {KommuneTilknytning(Kommune(it.verdi))} ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
        BYDEL ->  respons.gtBydel?.let {  BydelTilknytning(Bydel(it.verdi))}  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
        UDEFINERT -> UDEFINERT_GEO_TILKNYTNING
    }
}