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
    @DisplayName("Test at kode 6 kandidat ikke kan behandles av vanlig saksbehandler")
    fun kode6() {
        val k = Kandidat(FNR, STRENGT_FORTROLIG)
        val s = Saksbehandler(ATTRS, listOf(EntraGruppe(UUID.randomUUID(), FORTROLIG.gruppeNavn)))
        assertThrows<TilgangException> { MOTOR.vurderTilgang(k, s) }
    }


    companion object {
        private val ENHET = Enhetsnummer("4242")
        private val NAVID = NavId("Z999999")
        private val ATTRS = SaksbehandlerAttributter(UUID.randomUUID(),NAVID,"En","Saksbehandler", ENHET)
        private val FNR = Fødselsnummer("11111111111")
        private val MOTOR = StatiskRegelMotor()
    }
}