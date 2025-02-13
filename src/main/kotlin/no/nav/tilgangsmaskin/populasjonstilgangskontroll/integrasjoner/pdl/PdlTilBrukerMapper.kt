package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe
import org.slf4j.LoggerFactory

object PdlTilBrukerMapper {
    private val log = LoggerFactory.getLogger(javaClass)
    fun tilBruker(person: PdlPerson, gt: PdlGeoTilknytning, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG,
                    PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) })   {
                add(GlobalGruppe.STRENGT_FORTROLIG_GRUPPE)
            }
            else if (person.adressebeskyttelse.any { it.gradering == PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG })   {
                    add(GlobalGruppe.FORTROLIG_GRUPPE)
            }
            if (erSkjermet)  {
                add(GlobalGruppe.EGEN_ANSATT_GRUPPE)
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

    private fun tilGeoTilknytning(respons: PdlGeoTilknytning): GeoTilknytning = when (respons.gtType) {
        PdlGeoTilknytning.GTType.UTLAND ->  respons.gtLand?.let {
            GeoTilknytning.UtenlandskTilknytning(CountryCode.getByAlpha3Code(it.verdi))
        } ?: throw IllegalStateException("Utenlandsk tilknytning uten landkode")
        PdlGeoTilknytning.GTType.KOMMUNE -> respons.gtKommune?.let {
            GeoTilknytning.KommuneTilknytning(GeoTilknytning.Kommune(it.verdi))
        } ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
        PdlGeoTilknytning.GTType.BYDEL ->  respons.gtBydel?.let {
            GeoTilknytning.BydelTilknytning(GeoTilknytning.Bydel(it.verdi))
        }  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
        PdlGeoTilknytning.GTType.UDEFINERT -> GeoTilknytning.Companion.UdefinertGeoTilknytning
    }
}