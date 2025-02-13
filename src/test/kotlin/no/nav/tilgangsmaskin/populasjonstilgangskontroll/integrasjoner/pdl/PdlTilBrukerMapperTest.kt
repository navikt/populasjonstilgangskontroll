package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTKommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTLand
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Bostedsadresse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Bostedsadresse.UkjentBosted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Bostedsadresse.VegAdresse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Bostedsadresse.VegAdresse.Koordinater
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Folkeregisteridentifikator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class PdlTilBrukerMapperTest {

    private val fnr = Fødselsnummer("11111111111")

    @Test
    @DisplayName("Test at STRENGT_FORTROLIG_UTLAND fra Pdl krever medlemsskap i STRENGT_FORTROLIG_GRUPPEog at geotilknytning er UtenlandskTilknytning")
    fun strengtFortroligUtland()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(fnr,STRENGT_FORTROLIG_UTLAND), geoUtland(), false)) {
            assertEquals(ident, fnr)
            assertEquals(gruppeKrav.single(), STRENGT_FORTROLIG_GRUPPE)
            assertTrue(geoTilknytning is UtenlandskTilknytning)
        }
    }

    private fun geoUtland() = PdlGeoTilknytning(UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeoTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))

    fun pdlPerson(fnr: Fødselsnummer,gradering: AdressebeskyttelseGradering) : PdlPerson {
        val adressebeskyttelse = listOf(Adressebeskyttelse(gradering))
        val navn = listOf(PdlPerson.Navn("Ola", "Mellomnavn", "Nordmann"))
        return PdlPerson(adressebeskyttelse, navn, emptyList(), listOf(Folkeregisteridentifikator(fnr.verdi, "FNR")))
    }
    private fun pdlPerson1(fnr: Fødselsnummer,gradering: AdressebeskyttelseGradering) : PdlPerson {
        return pdlPerson(fnr,gradering).copy(bostedsadresse = listOf(Bostedsadresse(LocalDate.of(2020, 1, 1), "c/o Navn",
            VegAdresse(12345, "12", "A", "H0101", "Gateveien", "1234", "Tilleggsnavn", "1234", Koordinater(59.1234f, 10.1234f, null,1)),
            "Gateveien 12A", UkjentBosted("Ukjent Kommune"))))
    }
}
