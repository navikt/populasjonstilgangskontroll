package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTKommune
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTLand
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.KOMMUNE
import no.nav.tilgangsmaskin.bruker.pdl.PdlGeografiskTilknytning.GTType.UTLAND
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.AKTORID
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlIdenter.PdlIdent.PdlIdentGruppe.FOLKEREGISTERIDENT
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.PdlRespons.PdlPerson.PdlAdressebeskyttelse.PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration


@ActiveProfiles(TEST)
@ContextConfiguration(classes = [TestApp::class])

class BrukerMapperTest {
    
    private val aktørId = AktørId("1234567890123")

    private val vanligBrukerId = BrukerId("08526835670")

    private val brukerId = BrukerBuilder(vanligBrukerId).build().brukerId.verdi

    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG_UTLAND  krever medlemsskap i STRENGT_FORTROLIG_GRUPPE fra ansatt og at geotilknytning er UtenlandskTilknytning")
    fun strengtFortroligUtland() {
        with(tilBruker(tilPerson(brukerId,pipRespons(STRENGT_FORTROLIG_UTLAND)), false)) {
            assertThat(påkrevdeGrupper).containsExactly(GlobalGruppe.STRENGT_FORTROLIG_UTLAND)
            assertThat(geografiskTilknytning).isInstanceOf(GeografiskTilknytning.UtenlandskTilknytning::class.java)
        }
    }

    @Test
    @DisplayName("Test at behandling av brukere med STRENGT_FORTROLIG vil kreve medlemsskap i STRENGT_FORTROLIG_GRUPPE for ansatt og at geotilknytning er KommuneTilknytning")
    fun strengtFortroligKommune() {
        with(tilBruker(tilPerson(brukerId,pipRespons(STRENGT_FORTROLIG, geoKommune())), false)) {
            assertThat(påkrevdeGrupper).containsExactly(GlobalGruppe.STRENGT_FORTROLIG)
            assertThat(geografiskTilknytning).isInstanceOf(KommuneTilknytning::class.java)
        }
    }

    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT vil kreve medlemsskap i EGEN_ANSATT_GRUPPE for ansatt")
    fun egenAnsatt() {
        with(tilBruker(tilPerson(brukerId,pipRespons()), true)) {
            assertThat(påkrevdeGrupper).containsExactly(SKJERMING)
        }
    }

    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og STRENGT_FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og STRENGT_FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode6() {
        with(tilBruker(tilPerson(brukerId,pipRespons(STRENGT_FORTROLIG)), true)) {
            assertThat(påkrevdeGrupper).containsExactlyInAnyOrder(
                    SKJERMING,
                    GlobalGruppe.STRENGT_FORTROLIG)
        }
    }

    @Test
    @DisplayName("Test at behandling av brukere med EGEN_ANSATT og FORTROLIG vil kreve medlemsskap i EGEN_ANSATT_GRUPPE og FORTROLIG_GRUPPE for ansatt")
    fun egenAnsattKode7() {
        with(tilBruker(tilPerson(brukerId,pipRespons(FORTROLIG)), true)) {
            assertThat(påkrevdeGrupper).containsExactlyInAnyOrder(SKJERMING, GlobalGruppe.FORTROLIG)
        }
    }

    private fun geoUtland() = PdlGeografiskTilknytning(UTLAND, gtLand = GTLand("SWE"))
    private fun geoKommune() = PdlGeografiskTilknytning(KOMMUNE, gtKommune = GTKommune("1234"))



    fun pipRespons(
            gradering: PdlAdressebeskyttelseGradering? = null,
            geo: PdlGeografiskTilknytning = geoUtland()
                  ): PdlRespons {
        val adressebeskyttelse = gradering?.let {
            listOf(PdlAdressebeskyttelse(it))
        } ?: emptyList()
        return PdlRespons(
                PdlPerson(adressebeskyttelse),
                PdlIdenter(
                        listOf(
                                PdlIdent(brukerId, false, FOLKEREGISTERIDENT),
                                PdlIdent(aktørId.verdi, false, AKTORID))),
                geo
                         )
    }
}
