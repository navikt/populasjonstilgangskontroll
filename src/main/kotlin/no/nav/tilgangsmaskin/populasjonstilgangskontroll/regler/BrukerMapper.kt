package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import com.neovisionaries.i18n.CountryCode.getByAlpha3Code
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Bydel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Kommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.slf4j.LoggerFactory

object BrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
    fun tilBruker(fnr: Fødselsnummer, person: PdlPerson, gt: PdlGeoTilknytning, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) })   {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            else if (person.adressebeskyttelse.any { it.gradering == FORTROLIG})   {
                    add(FORTROLIG_GRUPPE)
            }
            if (erSkjermet)  {
                add(EGEN_ANSATT_GRUPPE)
            }
        }.toTypedArray().let {
            Bruker(fnr, tilNavn(person.navn),tilGeoTilknytning(gt), *it).also {
                log.trace(CONFIDENTIAL, "Mappet person {} til kandidat {}", person, it)
            }
        }

    private fun tilNavn(navn: List<PdlPerson.Navn>) =
        with(navn.first()) {
            Navn(fornavn, etternavn, mellomnavn)
        }

    private fun tilGeoTilknytning(respons: PdlGeoTilknytning): GeoTilknytning = when (respons.gtType) {
        UTLAND ->  respons.gtLand?.let {  UtenlandskTilknytning(getByAlpha3Code(it.verdi)) } ?: throw IllegalStateException("Utenlandsk tilknytning uten landkode")
        KOMMUNE -> respons.gtKommune?.let {KommuneTilknytning(Kommune(it.verdi))} ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
        BYDEL ->  respons.gtBydel?.let {  BydelTilknytning(Bydel(it.verdi))}  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
        UDEFINERT -> UdefinertGeoTilknytning
    }
}