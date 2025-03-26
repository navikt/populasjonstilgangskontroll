package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.neovisionaries.i18n.CountryCode.SE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.*

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterConstants.TEST
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeografiskTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlRespons


@ActiveProfiles(TEST)
@ContextConfiguration(classes = [TestApp::class])

class BrukerMapperTest {

    private val brukerId = TestData.vanligBruker.brukerId

    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG_UTLAND  krever medlemsskap i STRENGT_FORTROLIG_GRUPPE fra ansatt og at geotilknytning er UtenlandskTilknytning")
    fun strengtFortroligUtland()   {
        with(tilBruker(person(brukerId, pipRespons(STRENGT_FORTROLIG_UTLAND)), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geografiskTilknytning).isInstanceOf(UtenlandskTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG vil kreve medlemsskap i STRENGT_FORTROLIG_GRUPPE for ansatt og at geotilknytning er KommuneTilknytning")
    fun strengtFortroligKommune()   {
        with(tilBruker(person(brukerId, pipRespons(STRENGT_FORTROLIG, geoKommune())), false)) {
            assertThat(gruppeKrav).containsExactly(STRENGT_FORTROLIG_GRUPPE)
            assertThat(geografiskTilknytning).isInstanceOf(KommuneTilknytning::class.java)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT vil kreve medlemsskap i EGEN_ANSATT_GRUPPE for ansatt")
    fun egenAnsatt()   {
        with(tilBruker(person(brukerId, pipRespons()), true)) {
            assertThat(gruppeKrav).containsExactly(EGEN_ANSATT_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og STRENGT_FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og STRENGT_FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode6()   {
        with(tilBruker(person(brukerId, pipRespons(STRENGT_FORTROLIG)), true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,STRENGT_FORTROLIG_GRUPPE)
        }
    }
    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode7()   {
        with(tilBruker(person(brukerId, pipRespons(FORTROLIG)), true)) {
            assertThat(gruppeKrav).containsExactlyInAnyOrder(EGEN_ANSATT_GRUPPE,FORTROLIG_GRUPPE)
        }
    }

    private fun geoUtland() = PdlGeografiskTilknytning(PdlGeografiskTilknytning.GTType.UTLAND, gtLand = GTLand(SE.alpha3))
    private fun geoKommune() = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))


    fun pipRespons(gradering: PdlAdressebeskyttelseGradering? = null, geo: PdlGeografiskTilknytning = geoUtland()) : PdlRespons {
        val adressebeskyttelse = gradering?.let {
             listOf(PdlAdressebeskyttelse(it))
         }?: emptyList()
        return PdlRespons(PdlPerson(adressebeskyttelse), geografiskTilknytning = geo)
    }

    fun person(brukerId: BrukerId,respons: PdlRespons) = tilPerson(brukerId,respons)
}
