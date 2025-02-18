package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestRegler.Companion.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestRegler.Companion.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestRegler.Companion.kode7Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestRegler.Companion.motor

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.midlertidig.MidlertidigTilgangTjeneste
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste
    @MockK
    private lateinit var midlertidig: MidlertidigTilgangTjeneste
    @Test
    @DisplayName("Test at sjekkTilgang ikke sjekker midlertidig tilgang om det ikke kastes exception")
    fun testRegel() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        every { bruker.bruker(vanligBruker.ident) } returns vanligBruker
        val regel = RegelTjeneste(motor, bruker, ansatt, midlertidig)
        assertThatCode({regel.sjekkTilgang(vanligAnsatt.navId, vanligBruker.ident)}).doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.navId)
            bruker.bruker(vanligBruker.ident)
            midlertidig wasNot Called
        }
    }
    @Test
    @DisplayName("Test at harMidlertidigTilgang ikke blir kalt om det kastes en RegelException som er ikke er overstyrbar")
    fun testRegelIkkeOverstyrbar() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        every { bruker.bruker(kode7Bruker.ident) } returns kode7Bruker
        every { midlertidig.harMidlertidigTilgang(vanligAnsatt.navId, kode7Bruker.ident) } returns true
        val regel = RegelTjeneste(motor, bruker, ansatt, midlertidig)
        assertThrows<RegelException> {regel.sjekkTilgang(vanligAnsatt.navId, kode7Bruker.ident)}
        verify {
            ansatt.ansatt(vanligAnsatt.navId)
            bruker.bruker(kode7Bruker.ident)
            midlertidig wasNot Called
        }
    }
}