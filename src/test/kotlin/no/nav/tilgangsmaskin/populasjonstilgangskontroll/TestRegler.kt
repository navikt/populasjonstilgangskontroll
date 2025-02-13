package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Companion.UdefinertGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Navn
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.EgenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.StrengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.FortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class TestRegler {

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av kode 6 ansatt")
    fun kode7BrukerKode6Ansatt() {
        assertThrows<RegelException> { motor.vurderTilgang(kode7Bruker, kode6Ansatt) }.regel == fortroligRegel
    }

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av vanlig ansatt")
    fun kode7BrukerVanligAnsatt() {
        assertThrows<RegelException> { motor.vurderTilgang(kode7Bruker, vanligAnsatt) }.regel == fortroligRegel
    }

    @Test
    @DisplayName("Test at kode 7 bruker kan behandles av kode 7 ansatt")
    fun kode7brukerKode7Ansatt() {
        motor.vurderTilgang(kode7Bruker, kode7Ansatt)
    }
    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av kode 7 ansatt")
    fun kode6BrukerKode7Ansatt() {
        assertThrows<RegelException> { motor.vurderTilgang(kode6Bruker, kode7Ansatt) }.regel == strengtFortroligRegel
    }

    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av vanlig ansatt")
    fun kode6BrukerVanligAnsatt() {
        assertThrows<RegelException> { motor.vurderTilgang(kode6Bruker, vanligAnsatt) }.regel == strengtFortroligRegel
    }

    @Test
    @DisplayName("Test at kode 6 bruker kan behandles av kode 6 ansatt")
    fun kode6BrukerKode6Ansatt() {
        motor.vurderTilgang(kode6Bruker, kode6Ansatt)
    }
    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 6 ansatt")
    fun vanligBrukertKode6Ansatt() {
        motor.vurderTilgang(vanligBruker, kode6Ansatt)
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 7 ansatt")
    fun vanligBrukerKode7Ansatt() {
        motor.vurderTilgang(vanligBruker, kode7Ansatt)
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av vanlig ansatt")
    fun vanligBrukerVanligAnsatt() {
        motor.vurderTilgang(vanligBruker, vanligAnsatt)
    }

    @Test
    @DisplayName("Test at egen ansatt bruker kan behandles av egen ansatt ansatt")
    fun egenAnsattBrukerEgenAnsatt() {
        motor.vurderTilgang(ansattBruker, egenAnsatt)
    }

    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode7 ansatt")
    fun ansattBrukerKode7ansatt() {
        assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, kode7Ansatt) }.regel == fortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode6 ansatt")
    fun ansattBrukerKode6Ansatt() {
        assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, kode6Ansatt) }.regel == strengtFortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av vanlig ansatt")
    fun ansattBrukerVanligAnsatt() {
        assertThrows<RegelException> { motor.vurderTilgang(ansattBruker, vanligAnsatt) }.regel == egenAnsattRegel
    }

    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av egen ansatt")
    fun ansattKode6BrukerEgenAnsatt() {
        assertThrows<RegelException> { motor.vurderTilgang(ansattKode6Bruker, egenAnsatt) }.regel == strengtFortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 ikke kan behandles av egen ansatt")
    fun ansattKode7BrukerEgenAnsatt() {
        assertThrows<RegelException> { motor.vurderTilgang(ansattKode7Bruker, egenAnsatt) }.regel == fortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 kan behandles av kode 7 ansatt som også har ansatt gruppe")
    fun egenAnsattBrukerKode7Ansatt() {
        motor.vurderTilgang(ansattKode7Bruker, kode7EgenAnsatt)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 kan behandles av kode 6 ansatt som også gar har ansatt gruppe")
    fun ansattKode6BrukerKode6Ansatt() {
        motor.vurderTilgang(ansattKode6Bruker, kode6EgenAnsatt)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av kode 7 ansatt")
    fun ansattKode6BrukerKode7Ansatt() {
        assertThrows<RegelException> {motor.vurderTilgang(ansattKode6Bruker, kode7Ansatt) }.regel == strengtFortroligRegel
    }


    companion object {
        private val enhet = Enhetsnummer("4242")
        private val navid = NavId("Z999999")
        private val attributter = AnsattAttributter(randomUUID(),navid, Navn("En","Saksbehandler"), enhet)
        private val fnr = Fødselsnummer("11111111111")
        private val navn = Navn("Ola", "Nordmann")


        private val kode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, STRENGT_FORTROLIG_GRUPPE)
        private val kode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, FORTROLIG_GRUPPE)
        private val vanligBruker = Bruker(fnr,navn,UdefinertGeoTilknytning)
        private val ansattBruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_GRUPPE)
        private val ansattKode6Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
        private val ansattKode7Bruker = Bruker(fnr, navn,UdefinertGeoTilknytning, EGEN_GRUPPE, FORTROLIG_GRUPPE)

        private val strengtFortroligEntraGruppe = EntraGruppe(randomUUID(), "strengt fortrolig gruppe")
        private val fortroligEntraGruppe = EntraGruppe(randomUUID(), "fortrolig gruppe")
        private val egenAnsattEntraGruppe = EntraGruppe(randomUUID(), "egen gruppe")
        private val annenEntraGruppe = EntraGruppe(randomUUID(), "annen gruppe")

        private val kode7EgenAnsatt = Ansatt(attributter, fortroligEntraGruppe, egenAnsattEntraGruppe)
        private val kode6EgenAnsatt = Ansatt(attributter, strengtFortroligEntraGruppe, egenAnsattEntraGruppe)
        private val kode6Ansatt = Ansatt(attributter, strengtFortroligEntraGruppe)
        private val kode7Ansatt = Ansatt(attributter, fortroligEntraGruppe)
        private val egenAnsatt = Ansatt(attributter, egenAnsattEntraGruppe)
        private val vanligAnsatt = Ansatt(attributter, annenEntraGruppe)

        private val strengtFortroligRegel = StrengtFortroligRegel(strengtFortroligEntraGruppe.id)
        private val fortroligRegel = FortroligRegel(fortroligEntraGruppe.id)
        private val egenAnsattRegel = EgenAnsattRegel(egenAnsattEntraGruppe.id)

        private val motor = RegelMotor(strengtFortroligRegel,fortroligRegel, egenAnsattRegel)

    }
}