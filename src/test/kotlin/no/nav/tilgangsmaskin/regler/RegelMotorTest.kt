package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattFortroligAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattMedBarn
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattMedFar
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattMedPartner
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattMedSøsken
import no.nav.tilgangsmaskin.regler.ansatte.egenAnsattStrengtFortroligAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.fortroligAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.nasjonalAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.strengtFortroligAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.udefinertGeoAnsatt
import no.nav.tilgangsmaskin.regler.ansatte.vanligAnsatt
import no.nav.tilgangsmaskin.regler.brukere.ansattBruker
import no.nav.tilgangsmaskin.regler.brukere.egenAnsattFortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.egenAnsattStrengtFortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.fortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.skjermetBruker
import no.nav.tilgangsmaskin.regler.brukere.strengtFortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.ukjentBostedBruker
import no.nav.tilgangsmaskin.regler.brukere.vanligBruker
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource


@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@AutoConfigureObservability
@EnableConfigurationProperties(Grupper::class)
@ContextConfiguration(classes = [TestApp::class, TokenClaimsAccessor::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegelMotorTest {

    @MockkBean
    lateinit var holder: TokenValidationContextHolder

    @Autowired
    private lateinit var regelMotor: RegelMotor

    @Nested
    inner class SkjermingTester {

        @Test
        @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse *ikke* kan behandles av ansatt med medlemsskap kun i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattAvvises() {
            assertInstanceOf<StrengtFortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsatt, egenAnsattStrengtFortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at egen ansatt bruker med fortrolig beskyttelse ikke kan behandles av ansatt med medlemsskap i egen gruppe ansatt")
        fun egenAnsattFortroligBrukerEgenAnsattAvvises() {
            assertInstanceOf<FortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsatt, egenAnsattFortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at egen ansatt bruker med fortrolig beskyttelse kan behandles av ansatt med medlemsskap i egen ansatt gruppe som også har medlemsskap i fortrolig gruppe")
        fun egenAnsattFortroligBrukerEgenAnsattFortroligAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(egenAnsattFortroligAnsatt, egenAnsattFortroligBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse kan behandles av ansatt i egen ansatt gruppe som også har strengt fortrolig gruppe")
        fun egenAnsattStrengtFortroligBrukerEgenAnsattStrengtFortroligAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(egenAnsattStrengtFortroligAnsatt, egenAnsattStrengtFortroligBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse  ikke kan behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattStrengtFortroligBrukerFortroligAnsattAvvises() {
            assertInstanceOf<StrengtFortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(fortroligAnsatt, egenAnsattStrengtFortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe")
        fun egenAnsattBrukerEgenAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(egenAnsatt, skjermetBruker)
            }.doesNotThrowAnyException()
        }


        @Test
        @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av vanlig ansatt")
        fun ansattBrukerVanligAnsattAvvises() {
            assertInstanceOf<SkjermingRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(vanligAnsatt, ansattBruker)
                    }.regel)
        }

        @DisplayName("Test at saksbehandler ikke kan behandle seg selv")
        @Test
        fun egneDataAvvist() {
            assertInstanceOf<EgneDataRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsatt, ansattBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun egenAnsattBrukerFortroligAnsattAvvises() {
            assertInstanceOf<SkjermingRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(fortroligAnsatt, ansattBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun egenAnsattBrukerStrengtFortroligAnsattAvvises() {
            assertInstanceOf<SkjermingRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(strengtFortroligAnsatt, ansattBruker)
                    }.regel)
        }
    }

    @Nested
    inner class FortroligTester {
        @Test
        @DisplayName("Fortrolig bruker kan ikke behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun fortroligAvvist() {
            assertInstanceOf<FortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(strengtFortroligAnsatt, fortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Bruker med fortrolig beskyttelse kan ikke behandles av vanlig ansatt")
        fun fortroligAvvist1() {
            assertInstanceOf<FortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(vanligAnsatt, fortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Fortrolig bruker kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun fortroligOK() {
            assertThatCode {
                regelMotor.kompletteRegler(fortroligAnsatt, fortroligBruker)
            }.doesNotThrowAnyException()
        }
    }

    @Nested
    inner class GeoTester {
        @Test
        @DisplayName("Ansatt med nasjonal tilgang kan behandle vanlig bruker")
        fun geoNorgeNasjonal() {
            assertThatCode {
                regelMotor.kompletteRegler(nasjonalAnsatt, vanligBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Ansatt med manglende geografisk tilknytning kan behandle bruker med geografisk tilknytning")
        fun brukerMedManglendeGeografiskTilknytningAnsattMedSammeRolleOK() {
            assertThatCode {
                regelMotor.kompletteRegler(udefinertGeoAnsatt, ukjentBostedBruker)
            }.doesNotThrowAnyException()
        }
    }

    @Nested
    inner class NærståendeTester {

        @Test
        @DisplayName("Ansatt kan ikke behandle egen partner")
        fun egenPartnerAvvist() {
            assertInstanceOf<PartnerRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsattMedPartner, vanligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne barn")
        fun egneBarnAvvist() {
            assertInstanceOf<ForeldreOgBarnRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsattMedBarn, vanligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle egne foreldre")
        fun egneForeldreAvvist() {
            assertInstanceOf<ForeldreOgBarnRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsattMedFar, vanligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Ansatt kan ikke behandle søsken")
        fun søskenAvvist() {
            assertInstanceOf<SøskenRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(egenAnsattMedSøsken, vanligBruker)
                    }.regel)
        }
    }

    @Nested
    inner class StrengtFortroligTester {

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av vanlig ansatt")
        fun strengtAvvist() {
            assertInstanceOf<StrengtFortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(vanligAnsatt, strengtFortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun stregtAvvist1() {
            assertInstanceOf<StrengtFortroligRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(fortroligAnsatt, strengtFortroligBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig beskyttelse *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtOK() {
            assertThatCode {
                regelMotor.kompletteRegler(strengtFortroligAnsatt, strengtFortroligBruker)
            }.doesNotThrowAnyException()
        }
    }

    @Nested
    inner class UtlandTester {

        @Test
        @DisplayName("Test at bruker med strengt fortrolig utland beskyttelse *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun strengtUtlandOK() {
            assertThatCode {
                regelMotor.kompletteRegler(
                        strengtFortroligAnsatt,
                        brukere.strengtFortroligUtlandBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig utland beskyttelse ikke kan behandles av ansatt med medlemsskap i fortrolig gruppe")
        fun strengtUtlandAvvist() {
            assertInstanceOf<StrengtFortroligUtlandRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(
                                fortroligAnsatt,
                                brukere.strengtFortroligUtlandBruker)
                    }.regel)
        }

        @Test
        @DisplayName("Test at bruker med strengt fortrolig utland beskyttelse ikke kan behandles av vanlig ansatt")
        fun strengtUtlandAvvist1() {
            assertInstanceOf<StrengtFortroligUtlandRegel>(
                    assertThrows<RegelException> {
                        regelMotor.kompletteRegler(
                                vanligAnsatt,
                                brukere.strengtFortroligUtlandBruker)
                    }.regel)
        }
    }

    @Nested
    inner class VanligBrukerTest {

        @Test
        @DisplayName("Test at vanlig bruker *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
        fun vanligBrukertStrengtFortroligAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(strengtFortroligAnsatt, vanligBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Test at vanlig bruker *kan* behandles av ansatt med medlemsskap i fortrolig grupe")
        fun vanligBrukerFortroligAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(fortroligAnsatt, vanligBruker)
            }.doesNotThrowAnyException()
        }

        @Test
        @DisplayName("Test at vanlig bruker *kan* behandles av vanlig ansatt")
        fun vanligBrukerVanligAnsattOK() {
            assertThatCode {
                regelMotor.kompletteRegler(vanligAnsatt, vanligBruker)
            }.doesNotThrowAnyException()
        }
    }
}