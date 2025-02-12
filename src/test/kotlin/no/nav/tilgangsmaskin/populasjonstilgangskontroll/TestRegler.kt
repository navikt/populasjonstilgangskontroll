package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt.AnsattAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Companion.UDEFINERT_GEO_TILKNYTNING
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
    @DisplayName("Test at kode 7 kandidat ikke kan behandles av kode 6 saksbehandler")
    fun kode7kandidatIkkeKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE7KANDIDAT, KODE6SB) }
    }

    @Test
    @DisplayName("Test at kode 7 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode7kandidatIkkeVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE7KANDIDAT, VANLIGSB) }
    }

    @Test
    @DisplayName("Test at kode 7 kandidat kan behandles av kode 7 saksbehandler")
    fun kode7kandidatKode7() {
        MOTOR.vurderTilgang(KODE7KANDIDAT, KODE7SB)
    }
    @Test
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av kode 7 saksbehandler")
    fun kode6kandidatIkkeKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE6Bruker, KODE7SB) }
    }

    @Test
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode6kandidatIkkeVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE6Bruker, VANLIGSB) }
    }

    @Test
    @DisplayName("Test at kode 6 kandidat kan behandles av kode 6 saksbehandler")
    fun kode6kandidatKode6() {
        MOTOR.vurderTilgang(KODE6Bruker, KODE6SB)
    }
    @Test
    @DisplayName("Test at ubeskyttet kandidat kan behandles av kode 6 saksbehandler")
    fun vanligKandidatKode6() {
        MOTOR.vurderTilgang(VANLIGKANDIDAT, KODE6SB)
    }

    @Test
    @DisplayName("Test at ubeskyttet kandidat kan behandles av kode 7 saksbehandler")
    fun vanligKandidatKode7() {
        MOTOR.vurderTilgang(VANLIGKANDIDAT, KODE7SB)
    }

    @Test
    @DisplayName("Test at ubeskyttet kandidat kan behandles av vanlig saksbehandler")
    fun vanligKandidatVanlig() {
        MOTOR.vurderTilgang(VANLIGKANDIDAT, VANLIGSB)
    }

    @Test
    @DisplayName("Test at egen ansatt kandidat kan behandles av egen ansatt saksbehandler")
    fun egenAnsattOK() {
        MOTOR.vurderTilgang(ANSATTKANDIDAT, EGENSB)
    }

    @Test
    @DisplayName("Test at egen ansatt kandidat ikke kan behandles av kode7 saksbehandler")
    fun egenAnsattKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKANDIDAT, KODE7SB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat ikke kan behandles av kode6 saksbehandler")
    fun egenAnsattKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKANDIDAT, KODE6SB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat ikke kan behandles av vanlig saksbehandler")
    fun egenAnsattVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKANDIDAT, VANLIGSB) }
    }

    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 6 ikke kan behandles av egen ansatt saksbehandler")
    fun egenAnsattErOgsåKode6() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKODE6Bruker, EGENSB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 7 ikke kan behandles av egen ansatt saksbehandler")
    fun egenAnsattErOgsåKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKODE7Bruker, EGENSB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 7 kan behandles av kode 7 saksbehandler som også har ansatt gruppe")
    fun egenAnsattKode7SB() {
        MOTOR.vurderTilgang(ANSATTKODE7Bruker, KODE7OGEGENSB)
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 6 kan behandles av kode 6 saksbehandler som også gar hgeb ansatt gruppe")
    fun egenAnsattKode6OK() {
        MOTOR.vurderTilgang(ANSATTKODE6Bruker, KODE6OGEGENSB)
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 6 ikke kan behandles av kode 7 saksbehandler")
    fun egenAnsattKode6SB() {
        assertThrows<RegelException> {MOTOR.vurderTilgang(ANSATTKODE6Bruker, KODE7SB) }
    }


    companion object {
        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val ATTRS = AnsattAttributter(randomUUID(),NAVID,Navn("En","Saksbehandler"), ENHET)
        private val FNR = Fødselsnummer("11111111111")
        private val NAVN = Navn("Ola", "Nordmann")


        private val KODE6Bruker = Bruker(FNR, NAVN,UDEFINERT_GEO_TILKNYTNING, STRENGT_FORTROLIG_GRUPPE)
        private val KODE7KANDIDAT = Bruker(FNR, NAVN,UDEFINERT_GEO_TILKNYTNING, FORTROLIG_GRUPPE)
        private val VANLIGKANDIDAT = Bruker(FNR,NAVN,UDEFINERT_GEO_TILKNYTNING)
        private val ANSATTKANDIDAT = Bruker(FNR, NAVN,UDEFINERT_GEO_TILKNYTNING, EGEN_GRUPPE)
        private val ANSATTKODE6Bruker = Bruker(FNR, NAVN,UDEFINERT_GEO_TILKNYTNING, EGEN_GRUPPE, STRENGT_FORTROLIG_GRUPPE)
        private val ANSATTKODE7Bruker = Bruker(FNR, NAVN,UDEFINERT_GEO_TILKNYTNING, EGEN_GRUPPE, FORTROLIG_GRUPPE)

        private val STRENGT_FORTROLIG_ENTRA_GRUPPE = EntraGruppe(randomUUID(), "strengt fortrolig gruppe")
        private val FORTROLIG_ENTRA_GRUPPE = EntraGruppe(randomUUID(), "fortrolig gruppe")
        private val EGEN_ENTRA_GRUPPE = EntraGruppe(randomUUID(), "egen gruppe")
        private val ANNEN_ENTRA_GRUPPE = EntraGruppe(randomUUID(), "annen gruppe")

        private val KODE7OGEGENSB = Ansatt(ATTRS, FORTROLIG_ENTRA_GRUPPE, EGEN_ENTRA_GRUPPE)
        private val KODE6OGEGENSB = Ansatt(ATTRS, STRENGT_FORTROLIG_ENTRA_GRUPPE, EGEN_ENTRA_GRUPPE)
        private val KODE6SB = Ansatt(ATTRS, STRENGT_FORTROLIG_ENTRA_GRUPPE)
        private val KODE7SB = Ansatt(ATTRS, FORTROLIG_ENTRA_GRUPPE)
        private val EGENSB = Ansatt(ATTRS, EGEN_ENTRA_GRUPPE)
        private val VANLIGSB = Ansatt(ATTRS, ANNEN_ENTRA_GRUPPE)

        private val MOTOR = RegelMotor(StrengtFortroligRegel(STRENGT_FORTROLIG_ENTRA_GRUPPE.id), FortroligRegel(
            FORTROLIG_ENTRA_GRUPPE.id),EgenAnsattRegel(EGEN_ENTRA_GRUPPE.id))

    }
}