package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import java.util.*
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.Enhetsnummer
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.graph.EntraGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Kommune
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
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
import kotlin.test.BeforeTest
import kotlin.test.Test


@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureObservability
@EnableConfigurationProperties(GlobaleGrupper::class)
@ContextConfiguration(classes = [TestApp::class, Token::class])
@TestInstance(PER_CLASS)
class RegelMotorTest {

    private val brukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")

    @MockkBean
    lateinit var holder: TokenValidationContextHolder

    @Autowired
    private lateinit var registry: MeterRegistry
    
    @MockkBean
    private lateinit var token: Token
    private lateinit var logger: RegelMotorLogger


    @Autowired
    private lateinit var regelMotor: RegelMotor

    @BeforeTest
    fun before() {
        logger = RegelMotorLogger(AvvisningTeller(registry,token))
        every { token.system } returns "test"
        every { token.systemNavn } returns "test"
    }
    @Nested
    @TestInstance(PER_CLASS)
    inner class SkjermingTester {

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap kun i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
            forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen gruppe ansatt")
        fun egenAnsattFortroligBrukerEgenAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
            forventAvvistAv<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker med fortrolig beskyttelse kan behandles av ansatt med medlemsskap i egen ansatt gruppe som også har medlemsskap i fortrolig gruppe")
        fun egenAnsattFortroligBrukerEgenAnsattFortroligAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG, SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG, SKJERMING).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan behandles av ansatt i egen ansatt gruppe som også har strengt fortrolig gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattStrengtFortroligAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Egen ansatt bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG, SKJERMING).build()
            forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattBrukerEgenAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            regelMotor.kompletteRegler(ansatt, bruker)
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av vanlig ansatt")
        fun ansattBrukerVanligAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            forventAvvistAv<SkjermingRegel>(ansatt, bruker)
        }

        @DisplayName("Ansatt kan ikke behandle seg selv")
        @Test
        fun egneDataAvvist() {
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(SKJERMING).bruker(bruker).build()
            forventAvvistAv<EgneDataRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun egenAnsattBrukerFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            forventAvvistAv<SkjermingRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Egen ansatt bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun egenAnsattBrukerStrengtFortroligAnsattAvvises() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(SKJERMING).build()
            forventAvvistAv<SkjermingRegel>(ansatt, bruker)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class FortroligTester {


        @Test
        @DisplayName("Fortrolig bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun fortroligAvvist() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
            forventAvvistAv<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Bruker med fortrolig beskyttelse kan ikke behandles av vanlig ansatt")
        fun fortroligAvvist1() {
            val ansatt = AnsattBuilder(ansattId).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
            forventAvvistAv<FortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Fortrolig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun fortroligOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(FORTROLIG).build()
            assertThat(ansatt kanBehandle bruker).isTrue
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
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(NASJONAL).build()
            val bruker = BrukerBuilder(brukerId).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Ansatt med manglende geografisk tilknytning gruppe kan behandle bruker uten kjent geografisk tilknytning")
        fun ukjentBosted() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(UKJENT_BOSTED).build()
            val bruker = BrukerBuilder(brukerId, UkjentBosted()).kreverMedlemskapI(UKJENT_BOSTED).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Ansatt med tilgang som samme GT som bruker kan behandle denne")
        fun geoOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).medMedlemskapI(SKJERMING).build()
            val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune(enhet.verdi))).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Ansatt uten tilgang som samme GT som bruker kan ikke behandle denne")
        fun geoAvslått() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(enhetGruppe).build()
            val bruker = BrukerBuilder(brukerId).gt(KommuneTilknytning(Kommune("9999"))).build()
            forventAvvistAv<NorgeRegel>(ansatt, bruker)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class NærståendeTester {

        private val annenAnsattBrukerId = BrukerId("08526835644")


        @Test
        @DisplayName("Ansatt kan ikke behandle noen de har felles barn med")
        fun fellesBarnAvvist() {
            val barn = BrukerId("08526835649")
            val far = BrukerBuilder(annenAnsattBrukerId).barn(setOf(barn)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(far).build()
            val mor = BrukerBuilder(brukerId).barn(setOf(barn)).build()
            forventAvvistAv<FellesBarnRegel>(ansatt, mor)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egen partner")
        fun egenPartnerAvvist() {
            val ansattBruker = BrukerBuilder(annenAnsattBrukerId).partnere(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val partner = BrukerBuilder(brukerId).build()
            forventAvvistAv<PartnerRegel>(ansatt, partner)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne barn")
        fun egneBarnAvvist() {
            val ansattBruker = BrukerBuilder(annenAnsattBrukerId).barn(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val barn = BrukerBuilder(brukerId).build()
            forventAvvistAv<ForeldreOgBarnRegel>(ansatt, barn)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne foreldre")
        fun egneForeldreAvvist() {
            val ansattBruker =
                BrukerBuilder(annenAnsattBrukerId).far(brukerId).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val far = BrukerBuilder(brukerId).build()
            forventAvvistAv<ForeldreOgBarnRegel>(ansatt, far)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne søsken")
        fun søskenAvvist() {
            val ansattBruker =
                BrukerBuilder(annenAnsattBrukerId).søsken(setOf(brukerId)).build()
            val ansatt = AnsattBuilder(ansattId).bruker(ansattBruker).build()
            val søsken = BrukerBuilder(brukerId).build()
            forventAvvistAv<SøskenRegel>(ansatt, søsken)
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class StrengtFortroligTester {


        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan ikke behandles av vanlig ansatt")
        fun strengtAvvist() {
            val ansatt = AnsattBuilder(ansattId).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
            forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun stregtAvvist1() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
            forventAvvistAv<StrengtFortroligRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }
    }

    @Nested
    inner class UtlandTester {

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtUtlandOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun strengtUtlandAvvist() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
            forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
        }

        @Test
        @DisplayName("Bruker med strengt fortrolig utland beskyttelse kan ikke behandles av vanlig ansatt")
        fun strengtUtlandAvvist1() {
            val ansatt = AnsattBuilder(ansattId).build()
            val bruker = BrukerBuilder(brukerId).kreverMedlemskapI(STRENGT_FORTROLIG_UTLAND).build()
            forventAvvistAv<StrengtFortroligUtlandRegel>(ansatt, bruker)
        }
    }

    @Nested
    inner class VanligBrukerTest {

        @Test
        @DisplayName("Vanlig bruker kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun vanligBrukertStrengtFortroligAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(STRENGT_FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }

        @Test
        @DisplayName("Vanlig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun vanligBrukerFortroligAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).medMedlemskapI(FORTROLIG).build()
            val bruker = BrukerBuilder(brukerId).build()
            assertThat(ansatt kanBehandle bruker).isTrue

        }

        @Test
        @DisplayName("Vanlig bruker kan behandles av vanlig ansatt")
        fun vanligBrukerVanligAnsattOK() {
            val ansatt = AnsattBuilder(ansattId).build()
            val bruker = BrukerBuilder(brukerId).build()
            assertThat(ansatt kanBehandle bruker).isTrue
        }
    }

    private inline fun <reified T : Regel> forventAvvistAv(ansatt: Ansatt, bruker: Bruker) {
        val regel = assertThrows<RegelException> {
            regelMotor.kompletteRegler(ansatt, bruker)
        }.regel
        assertInstanceOf<T>(regel)
    }

    private infix fun Ansatt.kanBehandle(bruker: Bruker): Boolean {
        assertThatCode {
            regelMotor.kompletteRegler(this, bruker)
        }.doesNotThrowAnyException()
        return true
    }
}