package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class PersonMapperTest {

    @Test
    @DisplayName("Test at behandling av brukere med uten  geotilknytning får UdefinertTilknytning")
    fun udefinert()   {
        assertThat(tilPerson(vanligBruker.brukerId, pdlRespons(geoUdefinert())).geoTilknytning).isInstanceOf(UdefinertTilknytning::class.java)
    }

    @Test
    @DisplayName("Test at behandling av brukere uten geotilknytning får UtenlandskTilknytning")
    fun utland()   {
        assertThat(tilPerson(vanligBruker.brukerId, pdlRespons(geoUtland())).geoTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
    }
    @Test
    @DisplayName("Test at behandling av brukere med kommunal geotilknytning får KommuneTilknytning")
    fun kommune()   {
        assertThat(tilPerson(vanligBruker.brukerId, pdlRespons(geoKommune())).geoTilknytning).isInstanceOf(KommuneTilknytning::class.java)
    }
    @Test
    @DisplayName("Test at behandling av brukere med bydels geotilknytning får BydelTilknytning")
    fun bydel()   {
        assertThat(tilPerson(vanligBruker.brukerId, pdlRespons(geoBydel())).geoTilknytning).isInstanceOf(BydelTilknytning::class.java)
    }

    private fun geoUtland() = PdlGeoTilknytning(UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeoTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))
    private fun geoBydel() = PdlGeoTilknytning(BYDEL, gtBydel = GTBydel("123456"))
    private fun geoUdefinert() = PdlGeoTilknytning(UDEFINERT)


    private fun pdlRespons(geo: PdlGeoTilknytning, gradering: PdlAdressebeskyttelseGradering? = null) : PdlRespons {
        val adressebeskyttelse = gradering?.let {
             listOf(PdlAdressebeskyttelse(it))
         }?: emptyList()
        return PdlRespons(PdlPerson(adressebeskyttelse), geografiskTilknytning = geo)
    }

}
