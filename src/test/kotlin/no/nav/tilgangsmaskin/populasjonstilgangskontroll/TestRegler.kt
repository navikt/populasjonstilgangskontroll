package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler.SaksbehandlerAttributter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.StatiskRegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class TestRegler {

    @Test
    @DisplayName("Test at kode 7 kandidat bare kan behandles av kode 7 saksbehandler")
    fun kode7kandidat() {
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE7KANDIDAT, KODE6SB) }
        assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE7KANDIDAT, VANLIGSB) }
        MOTOR.vurderTilgang(KODE7KANDIDAT, KODE7SB)
    }
    @Test
    @DisplayName("Test at kode 6 kandidat bare kan behandles av kode 6 saksbehandler")
    fun kode6kandidat() {
       assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE6KANDIDAT, KODE7SB) }
       assertThrows<TilgangException> { MOTOR.vurderTilgang(KODE6KANDIDAT, VANLIGSB) }
        MOTOR.vurderTilgang(KODE6KANDIDAT, KODE6SB)
    }
    @Test
    @DisplayName("Test at ubeskytett kandidat kan behandles av kode 6, 7 eller vanlig saksbehandler")
    fun vanligKandidat() {
        MOTOR.vurderTilgang(VANLIGKANDIDAT, KODE6SB)
        MOTOR.vurderTilgang(VANLIGKANDIDAT, KODE7SB)
        MOTOR.vurderTilgang(VANLIGKANDIDAT, VANLIGSB)
    }


    companion object {
        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val ATTRS = SaksbehandlerAttributter(UUID.randomUUID(),NAVID,"En","Saksbehandler", ENHET)
        private val FNR = Fødselsnummer("11111111111")
        private val MOTOR = StatiskRegelMotor()
        private val KODE6KANDIDAT = Kandidat(FNR, STRENGT_FORTROLIG)
        private val KODE7KANDIDAT = Kandidat(FNR, FORTROLIG)
        private val VANLIGKANDIDAT = Kandidat(FNR, INGEN)
        private val KODE6SB = Saksbehandler(ATTRS, listOf(EntraGruppe(STRENGT_FORTROLIG.gruppeId, STRENGT_FORTROLIG.gruppeNavn)))
        private val KODE7SB = Saksbehandler(ATTRS, listOf(EntraGruppe(FORTROLIG.gruppeId, FORTROLIG.gruppeNavn)))
        private val VANLIGSB = Saksbehandler(ATTRS, listOf(EntraGruppe(UUID.randomUUID(), "annen gruppe")))


    }
}