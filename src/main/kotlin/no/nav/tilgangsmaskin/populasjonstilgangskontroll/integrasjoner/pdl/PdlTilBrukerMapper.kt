package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.getByAlpha3Code
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.*

object PdlTilBrukerMapper {
    fun tilBruker(person: PdlPerson, gt: PdlGeoTilknytning, erSkjermet: Boolean) =
        mutableListOf<GlobalGruppe>().apply {
            if  (person.adressebeskyttelse.any { it.gradering in listOf(STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND) }) {
                add(STRENGT_FORTROLIG_GRUPPE)
            }
            else if (person.adressebeskyttelse.any { it.gradering == FORTROLIG })   {
                add(FORTROLIG_GRUPPE)
            }

            if (gt.gtType == UDEFINERT) {
                add(UDEFINERT_GEO_GRUPPE)
            }

            if (erSkjermet)  {
                add(EGEN_ANSATT_GRUPPE)
            }
        }.let {
            Bruker(tilBrukerId(person.folkeregisteridentifikator), tilGeoTilknytning(gt), it)
        }

    private fun tilBrukerId(ident: List<PdlPerson.Folkeregisteridentifikator>) =
        BrukerId(ident.first().identifikasjonsnummer)

    fun tilGeoTilknytning(geo: PdlGeoTilknytning?): GeoTilknytning =
        when (geo?.gtType) {
            UTLAND ->  geo.gtLand?.let {
                UtenlandskTilknytning(getByAlpha3Code(it.verdi)) } ?: UkjentBosted()
            KOMMUNE -> geo.gtKommune?.let {
                KommuneTilknytning(Kommune(it.verdi))
            } ?: throw IllegalStateException("Kommunal tilknytning uten kommunekode")
            BYDEL ->  geo.gtBydel?.let {
                BydelTilknytning(Bydel(it.verdi))
            }  ?: throw IllegalStateException("Bydelstilknytning uten bydelskode")
            else -> UdefinertGeoTilknytning
        }
}