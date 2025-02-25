package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTKommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTLand
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Adressebeskyttelse.AdressebeskyttelseGradering.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson.Folkeregisteridentifikator
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PdlTilBrukerMapperTest {

    private val brukerId = BrukerId("08526835671")

    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG_UTLAND  krever medlemsskap i STRENGT_FORTROLIG_GRUPPE fra ansatt og at geotilknytning er UtenlandskTilknytning")
    fun strengtFortroligUtland()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(brukerId,STRENGT_FORTROLIG_UTLAND), geoUtland(), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geoTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG vil kreve medlemsskap i STRENGT_FORTROLIG_GRUPPE for ansatt og at geotilknytning er KommuneTilknytning")
    fun strengtFortroligKommune()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(brukerId,STRENGT_FORTROLIG), geoKommune(), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geoTilknytning).isInstanceOf(KommuneTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT vil kreve medlemsskap i EGEN_ANSATT_GRUPPE for ansatt")
    fun egenAnsatt()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(brukerId), geoKommune(), true)) {
            assertThat(gruppeKrav).containsExactly(EGEN_ANSATT_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og STRENGT_FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og STRENGT_FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode6()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(brukerId,STRENGT_FORTROLIG), geoKommune(), true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,STRENGT_FORTROLIG_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode7()   {
        with(PdlTilBrukerMapper.tilBruker(pdlPerson(brukerId,FORTROLIG), geoKommune(), true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,FORTROLIG_GRUPPE)
        }
    }

    private fun geoUtland() = PdlGeoTilknytning(UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeoTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))

    fun pdlPerson(brukerId: BrukerId, gradering: AdressebeskyttelseGradering? = null) : PdlPerson {
        val adressebeskyttelse = gradering?.let{
            listOf(Adressebeskyttelse(gradering))
        }?: emptyList()
        return PdlPerson(adressebeskyttelse, emptyList(), listOf(Folkeregisteridentifikator(brukerId.verdi, "FNR")))
    }
}
