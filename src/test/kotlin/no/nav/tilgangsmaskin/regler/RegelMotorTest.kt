package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.Enhetsnummer
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.entra.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.test.Test


@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureObservability
@EnableConfigurationProperties(Grupper::class)
@ContextConfiguration(classes = [TestApp::class, TokenClaimsAccessor::class])
@TestInstance(PER_CLASS)
class RegelMotorTest {

    private val vanligBrukerId = BrukerId("08526835670")


    @MockkBean
    lateinit var holder: TokenValidationContextHolder

    @Autowired
    private lateinit var regelMotor: RegelMotor

    @Nested
    @TestInstance(PER_CLASS)
    inner class SkjermingTester {

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap kun i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattAvvises() {
            val ansatt = AnsattBuilder().medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG, SKJERMING).build()
            forventAvvist<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen gruppe ansatt")
        fun egenAnsattFortroligBrukerEgenAnsattAvvises() {
            val ansatt = AnsattBuilder().medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(FORTROLIG, SKJERMING).build()
            forventAvvist<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med fortrolig beskyttelse kan behandles av ansatt med medlemsskap i egen ansatt gruppe som også har medlemsskap i fortrolig gruppe")
        fun egenAnsattFortroligBrukerEgenAnsattFortroligAnsattOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG, SKJERMING).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(FORTROLIG, SKJERMING).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan behandles av ansatt i egen ansatt gruppe som også har strengt fortrolig gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattStrengtFortroligAnsattOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG, SKJERMING).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG, SKJERMING).build()
            forventAvvist<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattBrukerEgenAnsattOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(SKJERMING).build()
            regelMotor.kompletteRegler(ansatt, bruker)
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av vanlig ansatt")
        fun ansattBrukerVanligAnsattAvvises() {
            val ansatt = AnsattBuilder().build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(SKJERMING).build()
            forventAvvist<SkjermingRegel>(ansatt, bruker)
        }

        @DisplayName("Ansatt kan ikke behandle seg selv")
        @Test
        fun egneDataAvvist() {
            val bruker = BrukerBuilder(vanligBrukerId).krever(SKJERMING).build()
            val ansatt = AnsattBuilder().medMedlemskapI(SKJERMING).bruker(bruker).build()
            forventAvvist<EgneDataRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun egenAnsattBrukerFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(SKJERMING).build()
            forventAvvist<SkjermingRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun egenAnsattBrukerStrengtFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(SKJERMING).build()
            forventAvvist<SkjermingRegel>(ansatt, bruker)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class FortroligTester {


        @Test
        @DisplayName("Fortrolig bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun fortroligAvvist() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(FORTROLIG).build()
            forventAvvist<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Bruker med fortrolig beskyttelse kan ikke behandles av vanlig ansatt")
        fun fortroligAvvist1() {
            val ansatt = AnsattBuilder().build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(FORTROLIG).build()
            forventAvvist<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Fortrolig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun fortroligOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(FORTROLIG).build()
            forventOK(ansatt, bruker)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class GeoTester {

        private val enhet = Enhetsnummer("4242")
        private val enhetGruppe = EntraGruppe(UUID.randomUUID(), "XXX_GEO_${enhet.verdi}")

        @Test
        @DisplayName("Ansatt med nasjonal tilgang kan behandle vanlig bruker")
        fun nasjonal() {
            val ansatt = AnsattBuilder().medMedlemskapI(NASJONAL).build()
            val bruker = BrukerBuilder(vanligBrukerId).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Ansatt med manglende geografisk tilknytning gruppe kan behandle bruker uten kjent geografisk tilknytning")
        fun ukjentBosted() {
            val ansatt = AnsattBuilder().medMedlemskapI(UKJENT_BOSTED).build()
            val bruker = BrukerBuilder(vanligBrukerId, UkjentBosted()).krever(UKJENT_BOSTED).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Ansatt med tilgang som samme GT som bruker kan behandle denne")
        fun geoOK() {
            val ansatt = AnsattBuilder().grupper(enhetGruppe).build()
            val bruker = BrukerBuilder(vanligBrukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Ansatt uten tilgang som samme GT som bruker kan ikke behandle denne")
        fun geoAvslått() {
            val ansatt = AnsattBuilder().grupper(enhetGruppe).build()
            val bruker = BrukerBuilder(vanligBrukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
            forventAvvist<NorgeRegel>(ansatt, bruker)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class NærståendeTester {

        private val annenAnsattBrukerId = BrukerId("08526835644")

        @Test
        @DisplayName("Ansatt kan ikke behandle egen partner")
        fun egenPartnerAvvist() {
            val ansattBruker =
                BrukerBuilder(annenAnsattBrukerId).krever(SKJERMING).partnere(setOf(vanligBrukerId)).build()
            val ansatt = AnsattBuilder().bruker(ansattBruker).build()
            val partner = BrukerBuilder(vanligBrukerId).build()
            forventAvvist<PartnerRegel>(ansatt, partner)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne barn")
        fun egneBarnAvvist() {
            val ansattBruker =
                BrukerBuilder(annenAnsattBrukerId).krever(SKJERMING).barn(setOf(vanligBrukerId)).build()
            val ansatt = AnsattBuilder().bruker(ansattBruker).build()
            val barn = BrukerBuilder(vanligBrukerId).build()
            forventAvvist<ForeldreOgBarnRegel>(ansatt, barn)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne foreldre")
        fun egneForeldreAvvist() {
            val ansattBruker = BrukerBuilder(annenAnsattBrukerId).krever(SKJERMING).far(vanligBrukerId).build()
            val ansatt = AnsattBuilder().bruker(ansattBruker).build()
            val far = BrukerBuilder(vanligBrukerId).build()
            forventAvvist<ForeldreOgBarnRegel>(ansatt, far)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne søsken")
        fun søskenAvvist() {
            val ansattBruker =
                BrukerBuilder(annenAnsattBrukerId).krever(SKJERMING).søsken(setOf(vanligBrukerId)).build()
            val ansatt = AnsattBuilder().bruker(ansattBruker).build()
            val søsken = BrukerBuilder(vanligBrukerId).build()
            forventAvvist<SøskenRegel>(ansatt, søsken)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class StrengtFortroligTester {


        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan ikke behandles av vanlig ansatt")
        fun strengtAvvist() {
            val ansatt = AnsattBuilder().build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG).build()
            forventAvvist<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun stregtAvvist1() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG).build()
            forventAvvist<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG).build()
            forventOK(ansatt, bruker)
        }
    }

    @Nested
    inner class UtlandTester {

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtUtlandOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG_UTLAND).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun strengtUtlandAvvist() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG_UTLAND).build()
            forventAvvist<StrengtFortroligUtlandRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av vanlig ansatt")
        fun strengtUtlandAvvist1() {
            val ansatt = AnsattBuilder().build()
            val bruker = BrukerBuilder(vanligBrukerId).krever(STRENGT_FORTROLIG_UTLAND).build()
            forventAvvist<StrengtFortroligUtlandRegel>(ansatt, bruker)
        }
    }

    @Nested
    inner class VanligBrukerTest {

        @Test
        @DisplayName("Vanlig bruker kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun vanligBrukertStrengtFortroligAnsattOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).build()
            forventOK(ansatt, bruker)
        }

        @Test
        @DisplayName("Vanlig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun vanligBrukerFortroligAnsattOK() {
            val ansatt = AnsattBuilder().medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(vanligBrukerId).build()
            forventOK(ansatt, bruker)

        }

        @Test
        @DisplayName("Vanlig bruker kan behandles av vanlig ansatt")
        fun vanligBrukerVanligAnsattOK() {
            val ansatt = AnsattBuilder().build()
            val bruker = BrukerBuilder(vanligBrukerId).build()
            forventOK(ansatt, bruker)
        }
    }

    private inline fun <reified T : Regel> forventAvvist(ansatt: Ansatt, bruker: Bruker) =
        assertInstanceOf<T>(assertThrows<RegelException> {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.regel)

    private fun forventOK(ansatt: Ansatt, bruker: Bruker) {
        assertThatCode {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.doesNotThrowAnyException()
    }
}