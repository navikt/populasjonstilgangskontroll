package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter
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
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE6KANDIDAT, KODE7SB) }
    }

    @Test
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode6kandidatIkkeVanlig() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(KODE6KANDIDAT, VANLIGSB) }
    }

    @Test
    @DisplayName("Test at kode 6 kandidat kan behandles av kode 6 saksbehandler")
    fun kode6kandidatKode6() {
        MOTOR.vurderTilgang(KODE6KANDIDAT, KODE6SB)
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
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKODE6KANDIDAT, EGENSB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 7 ikke kan behandles av egen ansatt saksbehandler")
    fun egenAnsattErOgsåKode7() {
        assertThrows<RegelException> { MOTOR.vurderTilgang(ANSATTKODE7KANDIDAT, EGENSB) }
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 7 kan behandles av kode 7 saksbehandler som også har ansatt gruppe")
    fun egenAnsattKode7SB() {
        MOTOR.vurderTilgang(ANSATTKODE7KANDIDAT, KODE7OGEGENSB)
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 6 kan behandles av kode 6 saksbehandler som også gar hgeb ansatt gruppe")
    fun egenAnsattKode6OK() {
        MOTOR.vurderTilgang(ANSATTKODE6KANDIDAT, KODE6OGEGENSB)
    }
    @Test
    @DisplayName("Test at egen ansatt kandidat med kode 6 ikke kan behandles av kode 7 saksbehandler")
    fun egenAnsattKode6SB() {
        assertThrows<RegelException> {MOTOR.vurderTilgang(ANSATTKODE6KANDIDAT, KODE7SB) }
    }


    companion object {
        private val STRENGT_FORTROLIG_ID = randomUUID()
        private val FORTROLIG_ID = randomUUID()
        private val EGENANSATT_ID = randomUUID()


        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val ATTRS = SaksbehandlerAttributter(randomUUID(),NAVID,"En","Saksbehandler", ENHET)
        private val FNR = Fødselsnummer("11111111111")
        private val MOTOR = RegelMotor(StrengtFortroligRegel(STRENGT_FORTROLIG_ID), FortroligRegel(FORTROLIG_ID),EgenAnsattRegel(EGENANSATT_ID))
        private val KODE6KANDIDAT = Kandidat(FNR, STRENGT_FORTROLIG)
        private val KODE7KANDIDAT = Kandidat(FNR, FORTROLIG)
        private val VANLIGKANDIDAT = Kandidat(FNR, INGEN)
        private val ANSATTKANDIDAT = Kandidat(FNR, EGEN)
        private val ANSATTKODE6KANDIDAT = Kandidat(FNR, EGEN, STRENGT_FORTROLIG)
        private val ANSATTKODE7KANDIDAT = Kandidat(FNR, EGEN, FORTROLIG)
        private val FORTROLIG_GRUPPE = EntraGruppe(FORTROLIG_ID, "fortrolig gruppe")
        private val EGEN_GRUPPE = EntraGruppe(EGENANSATT_ID, "egen gruppe")
        private val STRENGT_FORTROLIG_GRUPPE = EntraGruppe(STRENGT_FORTROLIG_ID, "strengt fortrolig gruppe")
        private val KODE7OGEGENSB = Saksbehandler(ATTRS, FORTROLIG_GRUPPE, EGEN_GRUPPE)
        private val KODE6OGEGENSB = Saksbehandler(ATTRS, STRENGT_FORTROLIG_GRUPPE, EGEN_GRUPPE)
        private val KODE6SB = Saksbehandler(ATTRS, STRENGT_FORTROLIG_GRUPPE)
        private val KODE7SB = Saksbehandler(ATTRS, FORTROLIG_GRUPPE)
        private val EGENSB = Saksbehandler(ATTRS, EGEN_GRUPPE)
        private val VANLIGSB = Saksbehandler(ATTRS, EntraGruppe(randomUUID(), "annen gruppe"))
    }
}