package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import com.ninjasquad.springmockk.MockkBean
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.annenAnsattBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ansattBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattStrengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.enhetAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.enhetBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.annenEnhetBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattStrengtFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattMedFamilie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.nasjonalAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.udefinertGeoAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterConstants.TEST
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource


@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@ContextConfiguration(classes = [TestApp::class])
class RegelMotorTest {

    @MockkBean
    lateinit var avdød: AvdødOppslagTeller
    @MockkBean
    lateinit var søsken : SøskenOppslagTeller
    @Autowired
    lateinit var regelMotor: RegelMotor

    @Test
    @DisplayName("Test at fortrolig bruker *ikke* kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun fortroligBrukerStrengtFortroligAnsattAvvises() {
        assertInstanceOf<FortroligRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(strengtFortroligAnsatt, fortroligBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at saksbehandler ikke kan behandle egen familie")
    fun egenFamilieAvvises() {
        assertInstanceOf<ForeldreOgBarnRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(egenAnsattMedFamilie, vanligBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at saksbehandler ikke kan behandle seg selv")
    fun egneDataAvvises() {
        assertInstanceOf<EgneDataRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(egenAnsatt, ansattBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at bruker med fortrolig beskyttelse *ikke* kan behandles av vanlig ansatt")
    fun fortroligBrukerVanligAnsattAvvises() {
        assertInstanceOf<FortroligRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(vanligAnsatt, fortroligBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at bruker med fortrolig beskyttelse *kan* behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun fortroligBrukerFortroligAnsattOK() {
        assertThatCode {
            regelMotor.kompletteRegler(fortroligAnsatt, fortroligBruker)
        }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun strengtFortroligBrukerFortroligAnsattAvvises() {
        assertInstanceOf<StrengtFortroligRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(fortroligAnsatt, strengtFortroligBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av vanlig ansatt")
    fun strengtFortroligBrukerVanligAnsattAvvises() {
        assertInstanceOf<StrengtFortroligRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(vanligAnsatt, strengtFortroligBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun strengtFortroligBrukerStrengtFortroligAnsattOK() {
        assertThatCode {
            regelMotor.kompletteRegler(strengtFortroligAnsatt, strengtFortroligBruker)
        }.doesNotThrowAnyException()
    }
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

    @Test
    @DisplayName("Test at egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe")
    fun egenAnsattBrukerEgenAnsattOK() {
        assertThatCode {
            regelMotor.kompletteRegler(egenAnsatt, annenAnsattBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun egenAnsattBrukerFortroligAnsattAvvises() {
        assertInstanceOf<EgenAnsattRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(fortroligAnsatt, ansattBruker)
            }.regel)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun egenAnsattBrukerStrengtFortroligAnsattAvvises() {
        assertInstanceOf<EgenAnsattRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(strengtFortroligAnsatt, ansattBruker)
            }.regel)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av vanlig ansatt")
    fun ansattBrukerVanligAnsattAvvises() {
        assertInstanceOf<EgenAnsattRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(vanligAnsatt, ansattBruker)
            }.regel)
    }

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
    @DisplayName("Test at ansatt med manglende geografisk tilknytning kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattMedSammeRolleOK() {
        assertThatCode {
            regelMotor.kompletteRegler(udefinertGeoAnsatt, ukjentBostedBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten manglende geografisk tilknytning rolle ikke kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattUtenSammeRoleOK() {
        assertInstanceOf<UkjentBostedGeoRegel>(
            assertThrows<RegelException> {
            regelMotor.kompletteRegler(vanligAnsatt, ukjentBostedBruker)
        }.regel)
    }

    @Test
    @DisplayName("Test at ansatt med tilgang utland kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeOK() {
        assertThatCode {
            regelMotor.kompletteRegler(geoUtlandAnsatt, geoUtlandBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten tilgang utland ikke kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeUtenSammeRolle() {
        assertInstanceOf<UtlandUdefinertGeoRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(vanligAnsatt, geoUtlandBruker)
            }.regel)
    }

    @Test
    @DisplayName("Test at ansatt med nasjonal tilgang kan behandle vanlig bruker")
    fun geoNorgeNasjonal() {
        assertThatCode {
            regelMotor.kompletteRegler(nasjonalAnsatt, vanligBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med geo tilgang kan behandle vanlig bruker med samme GT")
    fun geoEnhetLik() {
        assertThatCode {
            regelMotor.kompletteRegler(enhetAnsatt, enhetBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med annen geo tilgang enn brukers ikke kan behandle denne")
    fun geoEnhetForskjelligAvvises() {
        assertInstanceOf<GeoNorgeRegel>(
            assertThrows<RegelException> {
                regelMotor.kompletteRegler(enhetAnsatt, annenEnhetBruker)
            }.regel)
    }
}