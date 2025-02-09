package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.DummyRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.DefaultRegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.StrengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.FortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TilgangException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class TestRegler {

    @Test
    @DisplayName("Test at kode 7 kandidat ikke kan behandles av kode 6 saksbehandler")
    fun kode7kandidatIkkeKode6() {
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE7KANDIDAT, KODE6SB) }
    }

    @Test
    @DisplayName("Test at kode 7 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode7kandidatIkkeVanlig() {
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE7KANDIDAT, VANLIGSB) }
    }

    @Test
    @DisplayName("Test at kode 7 kandidat kan behandles av kode 7 saksbehandler")
    fun kode7kandidatKode7() {
        MOTOR.vurderTilgang(KODE7KANDIDAT, KODE7SB)
    }
    @Test
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av kode 7 saksbehandler")
    fun kode6kandidatIkkeKode7() {
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE6KANDIDAT, KODE7SB) }
    }

    @Test
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode6kandidatIkkeVanlig() {
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE6KANDIDAT, VANLIGSB) }
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


    companion object {
        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val ATTRS = SaksbehandlerAttributter(UUID.randomUUID(),NAVID,"En","Saksbehandler", ENHET)
        private val FNR = Fødselsnummer("11111111111")
        private val MOTOR = DefaultRegelMotor(StrengtFortroligRegel(), FortroligRegel(),DummyRegel())
        private val KODE6KANDIDAT = Kandidat(FNR, STRENGT_FORTROLIG)
        private val KODE7KANDIDAT = Kandidat(FNR, FORTROLIG)
        private val VANLIGKANDIDAT = Kandidat(FNR)
        private val KODE6SB = Saksbehandler(ATTRS, EntraGruppe(STRENGT_FORTROLIG.gruppeId, STRENGT_FORTROLIG.gruppeNavn))
        private val KODE7SB = Saksbehandler(ATTRS, EntraGruppe(FORTROLIG.gruppeId, FORTROLIG.gruppeNavn))
        private val VANLIGSB = Saksbehandler(ATTRS, EntraGruppe(UUID.randomUUID(), "annen gruppe"))


    }
}