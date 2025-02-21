package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.ansattBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.ansattKode6Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.ansattKode7Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.egenAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.egenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.enhetAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.enhetBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.enhetBruker1
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.fortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.geoNorgeRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.geoUtlandAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.geoUtlandRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode6Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode6Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode6EgenAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode7Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode7Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.kode7EgenAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.nasjonalAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.strengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.udefinertGeoAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.ukjentBostedGeoRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RegelMotorTest {


    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av kode 6 ansatt")
    fun kode7BrukerKode6Ansatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.alleRegler(kode6Ansatt, kode7Bruker) }.regel)
        assertThat(fortroligRegel.test(kode7Bruker, kode6Ansatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av vanlig ansatt")
    fun kode7BrukerVanligAnsatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.alleRegler(vanligAnsatt, kode7Bruker) }.regel)
        assertThat(fortroligRegel.test(kode7Bruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 7 bruker kan behandles av kode 7 ansatt")
    fun kode7brukerKode7Ansatt() {
        assertThatCode { motor.alleRegler(kode7Ansatt, kode7Bruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av kode 7 ansatt")
    fun kode6BrukerKode7Ansatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.alleRegler(kode7Ansatt, kode6Bruker) }.regel)
        assertThat(strengtFortroligRegel.test(kode6Bruker, kode7Ansatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av vanlig ansatt")
    fun kode6BrukerVanligAnsatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.alleRegler(vanligAnsatt, kode6Bruker) }.regel)
        assertThat(strengtFortroligRegel.test(kode6Bruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 6 bruker kan behandles av kode 6 ansatt")
    fun kode6BrukerKode6Ansatt() {
        assertThatCode { motor.alleRegler(kode6Ansatt, kode6Bruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 6 ansatt")
    fun vanligBrukertKode6Ansatt() {
        assertThatCode { motor.alleRegler(kode6Ansatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 7 ansatt")
    fun vanligBrukerKode7Ansatt() {
        assertThatCode({ motor.alleRegler(kode7Ansatt, vanligBruker) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av vanlig ansatt")
    fun vanligBrukerVanligAnsatt() {
        assertThatCode { motor.alleRegler(vanligAnsatt, vanligBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker kan behandles av egen ansatt ansatt")
    fun egenAnsattBrukerEgenAnsatt() {
        assertThatCode { motor.alleRegler(egenAnsatt, ansattBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode7 ansatt")
    fun ansattBrukerKode7ansatt() {
        assertEquals(egenAnsattRegel, assertThrows<RegelException> { motor.alleRegler(kode7Ansatt, ansattBruker) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, kode7Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode6 ansatt")
    fun ansattBrukerKode6Ansatt() {
        assertEquals(egenAnsattRegel,assertThrows<RegelException> { motor.alleRegler(kode6Ansatt, ansattBruker) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, kode6Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av vanlig ansatt")
    fun ansattBrukerVanligAnsatt() {
        assertEquals(egenAnsattRegel,assertThrows<RegelException> { motor.alleRegler(vanligAnsatt, ansattBruker) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av egen ansatt")
    fun ansattKode6BrukerEgenAnsatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.alleRegler(egenAnsatt,
            ansattKode6Bruker) }.regel)

    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 ikke kan behandles av egen ansatt")
    fun ansattKode7BrukerEgenAnsatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.alleRegler(egenAnsatt, ansattKode7Bruker) }.regel)
        assertThat(fortroligRegel.test(ansattKode7Bruker, egenAnsatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 kan behandles av kode 7 ansatt som også har ansatt gruppe")
    fun egenAnsattBrukerKode7Ansatt() {
        assertThatCode { motor.alleRegler(kode7EgenAnsatt, ansattKode7Bruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 kan behandles av kode 6 ansatt som også gar har ansatt gruppe")
    fun ansattKode6BrukerKode6Ansatt() {
        assertThatCode { motor.alleRegler(kode6EgenAnsatt, ansattKode6Bruker) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av kode 7 ansatt")
    fun ansattKode6BrukerKode7Ansatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> {motor.alleRegler(kode7Ansatt,
            ansattKode6Bruker) }.regel)
        assertThat(strengtFortroligRegel.test(ansattKode6Bruker, kode7Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at ansatt med manglende geografisk tilknytning kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattMedSamme() {
        assertThatCode { motor.alleRegler(udefinertGeoAnsatt, ukjentBostedBruker) }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten manglende geografisk tilknytning rolle ikke kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattUtenSammeRole() {
        assertEquals(ukjentBostedGeoRegel,assertThrows<RegelException> {motor.alleRegler(vanligAnsatt,
            ukjentBostedBruker)}.regel)
        assertThat(ukjentBostedGeoRegel.test(ukjentBostedBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at ansatt med tilgang utland  kan behandle bruker med geografisk utland")
    fun geoUtlandGruppe() {
        assertThatCode {
            motor.alleRegler(geoUtlandAnsatt, geoUtlandBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten tilgang utland ikke kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeUtenSammeRolle() {
        assertEquals(geoUtlandRegel,assertThrows<RegelException> {motor.alleRegler(vanligAnsatt, geoUtlandBruker)  }.regel)
        assertThat(geoUtlandRegel.test(geoUtlandBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at ansatt med nasjonal tilgang kan behandle vanlig bruker")
    fun geoNorgeNasjonal() {
        assertThatCode {
            motor.alleRegler(nasjonalAnsatt, vanligBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med geo tilgang kan behandle vanlig bruker med samme GT")
    fun geoEnhetLik() {
        assertThatCode {
            motor.alleRegler(enhetAnsatt, enhetBruker)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med annen geo tilgang enn brukers ikke kan behandle denne")
    fun geoEnhetForskjellig() {
        assertEquals(geoNorgeRegel,assertThrows<RegelException> {motor.alleRegler(enhetAnsatt, enhetBruker1)  }.regel)
        assertThat(geoNorgeRegel.test(enhetBruker1, enhetAnsatt)).isFalse()
    }
}

