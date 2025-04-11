package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.GTType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class PersonMapperTest {

    private val brukerId = vanligBruker.brukerId.verdi
    @Test
    @DisplayName("Test at behandling av brukere med uten  geotilknytning får UdefinertTilknytning")
    fun udefinert()   {
        assertThat(tilPerson(pdlRespons(geoUdefinert())).geoTilknytning).isInstanceOf(UdefinertTilknytning::class.java)
    }

    @Test
    @DisplayName("Test at behandling av brukere uten geotilknytning får UtenlandskTilknytning")
    fun utland()   {
        assertThat(tilPerson(pdlRespons(geoUtland())).geoTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
    }
    @Test
    @DisplayName("Test at behandling av brukere med kommunal geotilknytning får KommuneTilknytning")
    fun kommune()   {
        assertThat(tilPerson(pdlRespons(geoKommune())).geoTilknytning).isInstanceOf(KommuneTilknytning::class.java)
    }
    @Test
    @DisplayName("Test at behandling av brukere med bydels geotilknytning får BydelTilknytning")
    fun bydel()   {
        assertThat(tilPerson(pdlRespons(geoBydel())).geoTilknytning).isInstanceOf(BydelTilknytning::class.java)
    }

    private fun geoUtland() = PdlGeografiskTilknytning(UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))
    private fun geoBydel() = PdlGeografiskTilknytning(BYDEL, gtBydel = GTBydel("123456"))
    private fun geoUdefinert() = PdlGeografiskTilknytning(UDEFINERT)


    private fun pdlRespons(geo: PdlGeografiskTilknytning, gradering: PdlAdressebeskyttelseGradering? = null) : PdlRespons {
        val adressebeskyttelse = gradering?.let {
             listOf(PdlAdressebeskyttelse(it))
         }?: emptyList()
        return PdlRespons(PdlPerson(adressebeskyttelse), PdlIdenter(listOf(PdlIdent(brukerId,false, FOLKEREGISTERIDENT))), geo)
    }

}
