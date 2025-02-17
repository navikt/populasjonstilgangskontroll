package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import com.neovisionaries.i18n.CountryCode.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class RegelMotorTest {

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av kode 6 ansatt")
    fun kode7BrukerKode6Ansatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(kode7Bruker, kode6Ansatt) }.regel)
        assertThat(fortroligRegel.test(kode7Bruker, kode6Ansatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av vanlig ansatt")
    fun kode7BrukerVanligAnsatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(kode7Bruker, vanligAnsatt) }.regel)
        assertThat(fortroligRegel.test(kode7Bruker, vanligAnsatt)).isFalse()

    }

    @Test
    @DisplayName("Test at kode 7 bruker kan behandles av kode 7 ansatt")
    fun kode7brukerKode7Ansatt() {
        assertThatCode({ motor.vurderTilgang(kode7Bruker, kode7Ansatt) }).doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av kode 7 ansatt")
    fun kode6BrukerKode7Ansatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(kode6Bruker, kode7Ansatt) }.regel)
        assertThat(strengtFortroligRegel.test(kode6Bruker, kode7Ansatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av vanlig ansatt")
    fun kode6BrukerVanligAnsatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(kode6Bruker, vanligAnsatt) }.regel)
        assertThat(strengtFortroligRegel.test(kode6Bruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at kode 6 bruker kan behandles av kode 6 ansatt")
    fun kode6BrukerKode6Ansatt() {
        assertThatCode({ motor.vurderTilgang(kode6Bruker, kode6Ansatt) }).doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 6 ansatt")
    fun vanligBrukertKode6Ansatt() {
        assertThatCode({ motor.vurderTilgang(vanligBruker, kode6Ansatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 7 ansatt")
    fun vanligBrukerKode7Ansatt() {
        assertThatCode({ motor.vurderTilgang(vanligBruker, kode7Ansatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av vanlig ansatt")
    fun vanligBrukerVanligAnsatt() {
        assertThatCode({ motor.vurderTilgang(vanligBruker, vanligAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker kan behandles av egen ansatt ansatt")
    fun egenAnsattBrukerEgenAnsatt() {
        assertThatCode({ motor.vurderTilgang(ansattBruker, egenAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode7 ansatt")
    fun ansattBrukerKode7ansatt() {
        assertEquals(egenAnsattRegel, assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, kode7Ansatt) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, kode7Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode6 ansatt")
    fun ansattBrukerKode6Ansatt() {
        assertEquals(egenAnsattRegel,assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, kode6Ansatt) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, kode6Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av vanlig ansatt")
    fun ansattBrukerVanligAnsatt() {
        assertEquals(egenAnsattRegel,assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, vanligAnsatt) }.regel)
        assertThat(egenAnsattRegel.test(ansattBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av egen ansatt")
    fun ansattKode6BrukerEgenAnsatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(ansattKode6Bruker, egenAnsatt) }.regel)
        assertThat(strengtFortroligRegel.test(ansattKode6Bruker, egenAnsatt)).isFalse()

    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 ikke kan behandles av egen ansatt")
    fun ansattKode7BrukerEgenAnsatt() {
        assertEquals(fortroligRegel,assertThrows<RegelException> { motor.vurderTilgang(ansattKode7Bruker, egenAnsatt) }.regel)
        assertThat(fortroligRegel.test(ansattKode7Bruker, egenAnsatt)).isFalse()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 kan behandles av kode 7 ansatt som også har ansatt gruppe")
    fun egenAnsattBrukerKode7Ansatt() {
        assertThatCode({ motor.vurderTilgang(ansattKode7Bruker, kode7EgenAnsatt) }).doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 kan behandles av kode 6 ansatt som også gar har ansatt gruppe")
    fun ansattKode6BrukerKode6Ansatt() {
        assertThatCode({ motor.vurderTilgang(ansattKode6Bruker, kode6EgenAnsatt) }).doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av kode 7 ansatt")
    fun ansattKode6BrukerKode7Ansatt() {
        assertEquals(strengtFortroligRegel,assertThrows<RegelException> {motor.vurderTilgang(ansattKode6Bruker, kode7Ansatt) }.regel)
        assertThat(strengtFortroligRegel.test(ansattKode6Bruker, kode7Ansatt)).isFalse()
    }
    @Test
    @DisplayName("Test at ansatt med manglende geografisk tilknytning kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattMedSamme() {
        assertThatCode({ motor.vurderTilgang(ukjentBostedBruker, udefinertGeoAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten manglende geografisk tilknytning rolle ikke kan behandle bruker med geografisk tilknytning")
    fun brukerMedManglendeGeografiskTilknytningAnsattUtenSammeRole() {
        assertEquals(ukjentBostedGeoRegel,assertThrows<RegelException> {motor.vurderTilgang(ukjentBostedBruker, vanligAnsatt)}.regel)
        assertThat(ukjentBostedGeoRegel.test(ukjentBostedBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at ansatt med tilgang utland  kan behandle bruker med geografisk utland")
    fun geoUtlandGruppe() {
        assertThatCode({ motor.vurderTilgang(geoUtlandBruker, geoUtlandAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt uten tilgang utland ikke kan behandle bruker med geografisk utland")
    fun geoUtlandGruppeUtenSammeRolle() {
        assertEquals(geoUtlandRegel,assertThrows<RegelException> {motor.vurderTilgang(geoUtlandBruker, vanligAnsatt)  }.regel)
        assertThat(geoUtlandRegel.test(geoUtlandBruker, vanligAnsatt)).isFalse()
    }

    @Test
    @DisplayName("Test at ansatt med nasjonal tilgang kan behandle vanlig bruker")
    fun geoNorgeNasjonal() {
        assertThatCode({ motor.vurderTilgang(vanligBruker, nasjonalAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med geo tilgang kan behandle vanlig bruker med samme GT")
    fun geoEnhetLik() {
        assertThatCode({ motor.vurderTilgang(enhetBruker, enhetAnsatt) }).doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Test at ansatt med annen geo tilgang enn brukers ikke kan behandle denne")
    fun geoEnhetForskjellig() {
        assertEquals(geoNorgeRegel,assertThrows<RegelException> {motor.vurderTilgang(enhetBruker1, enhetAnsatt)  }.regel)
        assertThat(geoNorgeRegel.test(enhetBruker1, enhetAnsatt)).isFalse()
    }



    companion object {
        private val enhet = Enhetsnummer("4242")
        private val navid = NavId("Z999999")
        private val attributter = AnsattAttributter(randomUUID(),navid, Navn("En","Saksbehandler"), enhet)
        private val fnr = Fødselsnummer("11111111111")
        private val navn = Navn("Ola", "Nordmann")


        private val kode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, STRENGT_FORTROLIG_GRUPPE)
        val kode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, FORTROLIG_GRUPPE)
        val vanligBruker = Bruker(fnr,navn,UdefinertGeoTilknytning)
        private val ansattBruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE)
        private val ansattKode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
        private val ansattKode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_ANSATT_GRUPPE, FORTROLIG_GRUPPE)
        private val ukjentBostedBruker = Bruker(fnr, navn,UkjentBosted(), UDEFINERT_GEO_GRUPPE)
        private val geoUtlandBruker = Bruker(fnr, navn, UtenlandskTilknytning(SE), GEO_PERSON_UTLAND_GRUPPE)
        private val enhetBruker = Bruker(fnr, navn, KommuneTilknytning(Kommune(enhet.verdi)))
        private val enhetBruker1 = Bruker(fnr, navn, KommuneTilknytning(Kommune("4321")))



        private val strengtFortroligEntraGruppe = EntraGruppe(randomUUID(), "Strengt fortrolig gruppe")
        private val fortroligEntraGruppe = EntraGruppe(randomUUID(), "Fortrolig gruppe")
        private val egenAnsattEntraGruppe = EntraGruppe(randomUUID(), "egen gruppe")
        private val annenEntraGruppe = EntraGruppe(randomUUID(), "Annen gruppe")
        private val geoUtlandEntraGruppe = EntraGruppe(randomUUID(), "Geo utland gruppe")
        private val udefinertGruppe = EntraGruppe(randomUUID(), "Udefinert geo gruppe")
        private val nasjonalGruppe = EntraGruppe(randomUUID(), "Nsjonal gruppe")
        private val enhetGruppe = EntraGruppe(randomUUID(), "XXX_GEO_${enhet.verdi}")

        private val kode7EgenAnsatt = Ansatt(attributter, fortroligEntraGruppe, egenAnsattEntraGruppe)
        private val kode6EgenAnsatt = Ansatt(attributter, strengtFortroligEntraGruppe, egenAnsattEntraGruppe)
        private val kode6Ansatt = Ansatt(attributter, strengtFortroligEntraGruppe)
        private val kode7Ansatt = Ansatt(attributter, fortroligEntraGruppe)
        private val egenAnsatt = Ansatt(attributter, egenAnsattEntraGruppe)
        val vanligAnsatt = Ansatt(attributter, annenEntraGruppe)
        private val geoUtlandAnsatt = Ansatt(attributter, geoUtlandEntraGruppe)
        private val udefinertGeoAnsatt = Ansatt(attributter, udefinertGruppe)
        private val nasjonalAnsatt = Ansatt(attributter, nasjonalGruppe)
        private val enhetAnsatt = Ansatt(attributter, enhetGruppe)

        private val strengtFortroligRegel = StrengtFortroligRegel(strengtFortroligEntraGruppe.id)
        private val fortroligRegel = FortroligRegel(fortroligEntraGruppe.id)
        private val egenAnsattRegel = EgenAnsattRegel(egenAnsattEntraGruppe.id)
        private val ukjentBostedGeoRegel = UkjentBostedGeoRegel(udefinertGruppe.id)
        private val geoUtlandRegel = UtlandUdefinertGeoRegel(geoUtlandEntraGruppe.id)
        private val geoNorgeRegel = GeoNorgeRegel(nasjonalGruppe.id)

        val motor = RegelMotor(strengtFortroligRegel,fortroligRegel, egenAnsattRegel, ukjentBostedGeoRegel, geoUtlandRegel, geoNorgeRegel)

    }


}

