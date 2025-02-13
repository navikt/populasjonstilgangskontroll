package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.getByAlpha3Code
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.slf4j.LoggerFactory

object PdlTilBrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
    fun tilBruker(person: PdlPerson, gt: PdlGeoTilknytning, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            else if (person.adressebeskyttelse.any { it.gradering == FORTROLIG })   {
                add(FORTROLIG_GRUPPE)
            }
            if (erSkjermet)  {
                add(EGEN_ANSATT_GRUPPE)
            }
        }.toTypedArray().let {
            Bruker(tilFødselsnummer(person.folkeregisteridentifikator), tilNavn(person.navn), tilGeoTilknytning(gt), *it).also {
                log.trace(CONFIDENTIAL, "Mappet person {} til kandidat {}", person, it)
            }
        }

    private fun tilFødselsnummer(ident: List<PdlPerson.Folkeregisteridentifikator>) =
        Fødselsnummer(ident.first().identifikasjonsnummer)

    private fun tilNavn(navn: List<PdlPerson.Navn>) =
        with(navn.first()) {
            Navn(fornavn, etternavn, mellomnavn)
        }

    private fun tilGeoTilknytning(geo: PdlGeoTilknytning): GeoTilknytning =
        when (geo.gtType) {
            UTLAND ->  geo.gtLand?.let {
                UtenlandskTilknytning(getByAlpha3Code(it.verdi)) } ?: throw IllegalStateException("Utenlandsk tilknytning uten landkode")
            KOMMUNE -> geo.gtKommune?.let {
                KommuneTilknytning(Kommune(it.verdi))
            } ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
            BYDEL ->  geo.gtBydel?.let {
                BydelTilknytning(Bydel(it.verdi))
            }  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
            UDEFINERT -> UdefinertGeoTilknytning
        }
}