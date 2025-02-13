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
    fun kode7BrukerkkeKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(Kode7Bruker, Kode6Ansatt) }.regel == FortroligRegel
    }

    @Test
    @DisplayName("Test at kode 7 bruker ikke kan behandles av vanlig ansatt")
    fun kode7BrukerIkkeVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(Kode7Bruker, VanligAnsatt) }.regel == FortroligRegel
    }

    @Test
    @DisplayName("Test at kode 7 bruker kan behandles av kode 7 ansatt")
    fun kode7BrukerKode7() {
        MOTOR.vurderTilgang(Kode7Bruker, Kode7Ansatt)
    }
    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av kode 7 ansatt")
    fun kode6BrukerIkkeKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(Kode6Bruker, Kode7Ansatt) }.regel == StrengtFortroligRegel
    }

    @Test
    @DisplayName("Test at kode 6 bruker ikke kan behandles av vanlig ansatt")
    fun kode6BrukerIkkeVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(Kode6Bruker, VanligAnsatt) }.regel == StrengtFortroligRegel
    }

    @Test
    @DisplayName("Test at kode 6 bruker kan behandles av kode 6 ansatt")
    fun kode6BrukerKode6() {
        MOTOR.vurderTilgang(Kode6Bruker, Kode6Ansatt)
    }
    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 6 ansatt")
    fun vanligBrukertKode6() {
        MOTOR.vurderTilgang(VanligBruker, Kode6Ansatt)
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av kode 7 ansatt")
    fun vanligBrukerKode7() {
        MOTOR.vurderTilgang(VanligBruker, Kode7Ansatt)
    }

    @Test
    @DisplayName("Test at vanlig bruker kan behandles av vanlig ansatt")
    fun vanligBrukerVanlig() {
        MOTOR.vurderTilgang(VanligBruker, VanligAnsatt)
    }

    @Test
    @DisplayName("Test at egen ansatt bruker kan behandles av egen ansatt ansatt")
    fun egenAnsattOK() {
        MOTOR.vurderTilgang(AnsattBruker, EgenAnsatt)
    }

    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode7 ansatt")
    fun egenAnsattKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(AnsattBruker, Kode7Ansatt) }.regel == FortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av kode6 ansatt")
    fun egenAnsattKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(AnsattBruker, Kode6Ansatt) }.regel == StrengtFortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker ikke kan behandles av vanlig ansatt")
    fun egenAnsattVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(AnsattBruker, VanligAnsatt) }.regel == EgenAnsattRegel
    }

    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av egen ansatt")
    fun egenAnsattErOgsåKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(AnsattKode6Bruker, EgenAnsatt) }.regel == StrengtFortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 ikke kan behandles av egen ansatt")
    fun egenAnsattErOgsåKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(AnsattKode7Bruker, EgenAnsatt) }.regel == FortroligRegel
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 7 kan behandles av kode 7 ansatt som også har ansatt gruppe")
    fun egenAnsattKode7SB() {
        MOTOR.vurderTilgang(AnsattKode7Bruker, Kode7OgEgenAnsatt)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 kan behandles av kode 6 ansatt som også gar har ansatt gruppe")
    fun egenAnsattKode6OK() {
        MOTOR.vurderTilgang(AnsattKode6Bruker, Kode6OgEgenAnsatt)
    }
    @Test
    @DisplayName("Test at egen ansatt bruker med kode 6 ikke kan behandles av kode 7 ansatt")
    fun egenAnsattKode6SB() {
        assertThrows<RegelException> {MOTOR.vurderTilgang(AnsattKode6Bruker, Kode7Ansatt) }.regel == StrengtFortroligRegel
    }


    companion object {
        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val Attributter = AnsattAttributter(randomUUID(),NAVID, Navn("En","Saksbehandler"), ENHET)
        private val Fnr = Fødselsnummer("11111111111")
        private val Navn = Navn("Ola", "Nordmann")


        private val Kode6Bruker = Bruker(Fnr, Navn,UdefinertGeoTilknytning, STRENGT_FORTROLIG_GRUPPE)
        private val Kode7Bruker = Bruker(Fnr, Navn,UdefinertGeoTilknytning, FORTROLIG_GRUPPE)
        private val VanligBruker = Bruker(Fnr,Navn,UdefinertGeoTilknytning)
        private val AnsattBruker = Bruker(Fnr, Navn,UdefinertGeoTilknytning, EGEN_GRUPPE)
        private val AnsattKode6Bruker = Bruker(Fnr, Navn,UdefinertGeoTilknytning, EGEN_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
        private val AnsattKode7Bruker = Bruker(Fnr, Navn,UdefinertGeoTilknytning, EGEN_GRUPPE, FORTROLIG_GRUPPE)

        private val StrengtFortroligEntraGruppe = EntraGruppe(randomUUID(), "strengt fortrolig gruppe")
        private val FortroligEntraGruppe = EntraGruppe(randomUUID(), "fortrolig gruppe")
        private val EgenAnsattEntraGruppe = EntraGruppe(randomUUID(), "egen gruppe")
        private val AnnenEntraGruppe = EntraGruppe(randomUUID(), "annen gruppe")

        private val Kode7OgEgenAnsatt = Ansatt(Attributter, FortroligEntraGruppe, EgenAnsattEntraGruppe)
        private val Kode6OgEgenAnsatt = Ansatt(Attributter, StrengtFortroligEntraGruppe, EgenAnsattEntraGruppe)
        private val Kode6Ansatt = Ansatt(Attributter, StrengtFortroligEntraGruppe)
        private val Kode7Ansatt = Ansatt(Attributter, FortroligEntraGruppe)
        private val EgenAnsatt = Ansatt(Attributter, EgenAnsattEntraGruppe)
        private val VanligAnsatt = Ansatt(Attributter, AnnenEntraGruppe)

        private val StrengtFortroligRegel = StrengtFortroligRegel(StrengtFortroligEntraGruppe.id)
        private val FortroligRegel = FortroligRegel(FortroligEntraGruppe.id)
        private val EgenAnsattRegel = EgenAnsattRegel(EgenAnsattEntraGruppe.id)


        private val MOTOR = RegelMotor(StrengtFortroligRegel,FortroligRegel, EgenAnsattRegel)

    }
}