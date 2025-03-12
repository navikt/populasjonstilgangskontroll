package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.mockk.called
import io.mockk.verify
import io.mockk.verifyOrder
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.annenAnsattBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ansattBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattStrengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.enhetAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.enhetBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.annenEnhetBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoNorgeRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattStrengtFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattFortroligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenAnsattMedFamilie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egenFamilieRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.egneDataRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.nasjonalAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spiesMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyEgenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyGeoNorgeRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyGeoUtlandRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyStrengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.spyUkjentBostedGeoRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.udefinertGeoAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ukjentBostedGeoRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RegelMotorTest {


    @Test
    @DisplayName("Test at fortrolig bruker *ikke* kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun fortroligBrukerStrengtFortroligAnsattAvvises() {
        assertEquals(fortroligRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(strengtFortroligAnsatt, fortroligBruker)
            }.regel)
        assertThat(fortroligRegel.test(strengtFortroligAnsatt,fortroligBruker)).isFalse
        assertThrows<RegelException> {
            spiesMotor.kompletteRegler(strengtFortroligAnsatt, fortroligBruker)
        }
        verifyOrder {
            spyStrengtFortroligRegel.test(strengtFortroligAnsatt,fortroligBruker)
            spyFortroligRegel.test(strengtFortroligAnsatt,fortroligBruker)
        }
        verify {
            spyEgenAnsattRegel wasNot called
            spyGeoUtlandRegel wasNot called
            spyUkjentBostedGeoRegel wasNot called
            spyGeoNorgeRegel wasNot called
        }
    }

    @Test
    @DisplayName("Test at saksbehandler ikke kan behandle egen familie")
    fun egenFamilieAvvises() {
        assertEquals(
            egenFamilieRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(egenAnsattMedFamilie, vanligBruker)
            }.regel)
        assertThat(egenFamilieRegel.test(egenAnsattMedFamilie, vanligBruker)).isFalse
    }

    @Test
    @DisplayName("Test at saksbehandler ikke kan behandle seg selv")
    fun egneDataAvvises() {
        assertEquals(egneDataRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(egenAnsatt, ansattBruker)
            }.regel)
        assertThat(egneDataRegel.test(vanligAnsatt,ansattBruker)).isFalse
    }

    @Test
    @DisplayName("Test at bruker med fortrolig beskyttelse *ikke* kan behandles av vanlig ansatt")
    fun fortroligBrukerVanligAnsattAvvises() {
        assertEquals(fortroligRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(vanligAnsatt, fortroligBruker)
            }.regel)
        assertThat(fortroligRegel.test(vanligAnsatt,fortroligBruker)).isFalse
    }

    @Test
    @DisplayName("Test at bruker med fortrolig beskyttelse *kan* behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun fortroligBrukerFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(fortroligAnsatt, fortroligBruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun strengtFortroligBrukerFortroligAnsattAvvises() {
        assertEquals(strengtFortroligRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(fortroligAnsatt, strengtFortroligBruker)
            }.regel)
        assertThat(strengtFortroligRegel.test(fortroligAnsatt,strengtFortroligBruker)).isFalse
    }

    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *ikke* kan behandles av vanlig ansatt")
    fun strengtFortroligBrukerVanligAnsattAvvises() {
        assertEquals(strengtFortroligRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(vanligAnsatt, strengtFortroligBruker)
            }.regel)
        assertThat(strengtFortroligRegel.test(vanligAnsatt,strengtFortroligBruker)).isFalse
    }

    @Test
    @DisplayName("Test at bruker med strengt fortrolig beskyttelse *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun strengtFortroligBrukerStrengtFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(strengtFortroligAnsatt, strengtFortroligBruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at vanlig bruker *kan* behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun vanligBrukertStrengtFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(strengtFortroligAnsatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker *kan* behandles av ansatt med medlemsskap i fortrolig grupe")
    fun vanligBrukerFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(fortroligAnsatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker *kan* behandles av vanlig ansatt")
    fun vanligBrukerVanligAnsattOK() {
        assertThatCode { motor.kompletteRegler(vanligAnsatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker *kan* behandles av ansatt med medlemsskap i egen ansatt gruppe")
    fun egenAnsattBrukerEgenAnsattOK() {
        assertThatCode { motor.kompletteRegler(egenAnsatt, annenAnsattBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i fortrolig gruppe")
    fun egenAnsattBrukerFortroligAnsattAvvises() {
        assertEquals(egenAnsattRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(fortroligAnsatt,
                    ansattBruker)
            }.regel)
        assertThat(egenAnsattRegel.test(fortroligAnsatt,ansattBruker)).isFalse
    }
    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av ansatt med medlemsskap i strengt fortrolig gruppe")
    fun egenAnsattBrukerStrengtFortroligAnsattAvvises() {
        assertEquals(egenAnsattRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(strengtFortroligAnsatt, ansattBruker)
            }.regel)
        assertThat(egenAnsattRegel.test(strengtFortroligAnsatt,ansattBruker)).isFalse
    }
    @Test
    @DisplayName("Test at egen ansatt bruker *ikke* kan behandles av vanlig ansatt")
    fun ansattBrukerVanligAnsattAvvises() {
        assertEquals(egenAnsattRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(vanligAnsatt, ansattBruker)
            }.regel)
        assertThat(egenAnsattRegel.test(vanligAnsatt,ansattBruker)).isFalse
    }

    @Test
    @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse *ikke* kan behandles av ansatt med medlemsskap kun i egen ansatt gruppe")
    fun egenAnsattStrengtFortroligBrukerEgenAnsattAvvises() {
        assertEquals(strengtFortroligRegel, assertThrows<RegelException> {
            motor.kompletteRegler(egenAnsatt, egenAnsattStrengtFortroligBruker)
        }.regel)
        assertThat(strengtFortroligRegel.test(egenAnsatt,egenAnsattStrengtFortroligBruker)).isFalse
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med fortrolig beskyttelse ikke kan behandles av ansatt med medlemsskap i egen gruppe ansatt")
    fun egenAnsattFortroligBrukerEgenAnsattAvvises() {
        assertEquals(fortroligRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(egenAnsatt, egenAnsattFortroligBruker)
            }.regel)
        assertThat(fortroligRegel.test(egenAnsatt,egenAnsattFortroligBruker)).isFalse
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med fortrolig beskyttelse kan behandles av ansatt med medlemsskap i egen ansatt gruppe som også har medlemsskap i fortrolig gruppe")
    fun egenAnsattFortroligBrukerEgenAnsattFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(egenAnsattFortroligAnsatt, egenAnsattFortroligBruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse kan behandles av ansatt i egen ansatt gruppe som også har strengt fortrolig gruppe")
    fun egenAnsattStrengtFortroligBrukerEgenAnsattStrengtFortroligAnsattOK() {
        assertThatCode { motor.kompletteRegler(egenAnsattStrengtFortroligAnsatt, egenAnsattStrengtFortroligBruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med strengt fortrolig beskyttelse  ikke kan behandles av ansatt med medlemsskap i egen ansatt gruppe")
    fun egenAnsattStrengtFortroligBrukerFortroligAnsattAvvises() {
        assertEquals(strengtFortroligRegel, assertThrows<RegelException> {
            motor.kompletteRegler(fortroligAnsatt, egenAnsattStrengtFortroligBruker)
        }.regel)
        assertThat(strengtFortroligRegel.test(fortroligAnsatt,egenAnsattStrengtFortroligBruker)).isFalse
    }
    @Test
    @DisplayName("Test at ansatt med manglende geografisk tilknytning kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattMedSammeRolleOK() {
        assertThatCode { motor.kompletteRegler(udefinertGeoAnsatt, ukjentBostedBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten manglende geografisk tilknytning rolle ikke kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattUtenSammeRoleOK() {
        assertEquals(ukjentBostedGeoRegel, assertThrows<RegelException> {
            motor.kompletteRegler(vanligAnsatt, ukjentBostedBruker)
        }.regel)
        assertThat(ukjentBostedGeoRegel.test(vanligAnsatt,ukjentBostedBruker)).isFalse
    }

    @Test
    @DisplayName("Test at ansatt med tilgang utland kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeOK() {
        assertThatCode { motor.kompletteRegler(geoUtlandAnsatt, geoUtlandBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten tilgang utland ikke kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeUtenSammeRolle() {
        assertEquals(geoUtlandRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(vanligAnsatt, geoUtlandBruker)
            }.regel)
        assertThat(geoUtlandRegel.test(vanligAnsatt,geoUtlandBruker)).isFalse
    }

    @Test
    @DisplayName("Test at ansatt med nasjonal tilgang kan behandle vanlig bruker")
    fun geoNorgeNasjonal() {
        assertThatCode { motor.kompletteRegler(nasjonalAnsatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med geo tilgang kan behandle vanlig bruker med samme GT")
    fun geoEnhetLik() {
        assertThatCode { motor.kompletteRegler(enhetAnsatt, enhetBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med annen geo tilgang enn brukers ikke kan behandle denne")
    fun geoEnhetForskjelligAvvises() {
        assertEquals(geoNorgeRegel,
            assertThrows<RegelException> {
                motor.kompletteRegler(enhetAnsatt, annenEnhetBruker)
            }.regel)
        assertThat(geoNorgeRegel.test(enhetAnsatt,annenEnhetBruker)).isFalse
    }

    @Test
    @DisplayName("Sjekk at reglene er sorterte")
    fun sortert() {
        assertThat(motor.komplettRegelSett.regler).containsExactly(strengtFortroligRegel, fortroligRegel, egenAnsattRegel, egneDataRegel,egenFamilieRegel,geoUtlandRegel,ukjentBostedGeoRegel, geoNorgeRegel)
        assertThat(motor.kjerneRegelSett.regler).containsExactly(strengtFortroligRegel, fortroligRegel, egenAnsattRegel, egneDataRegel, egenFamilieRegel)
    }
}