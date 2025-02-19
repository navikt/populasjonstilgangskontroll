package no.nav.tilgangsmaskin.populasjonstilgangskontroll

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.egenAnsattRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.fnr
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.fortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.navid
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.strengtFortroligRegel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelExceptionHandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.BeforeTest

@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste
    @MockK
    private lateinit var midlertidig: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste

    private lateinit var errorHandler: RegelExceptionHandler


    @BeforeTest
    fun before() {
        errorHandler = RegelExceptionHandler(midlertidig)
        regel = RegelTjeneste(motor, bruker, ansatt,errorHandler)
    }
    @Test
    @DisplayName("Test at sjekk av midlertidig tilgang ikke gjøres om det ikke kastes en exception fra en regel")
    fun testIngenMidlertidigSjekk() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        every { bruker.bruker(vanligBruker.ident) } returns vanligBruker
        assertThatCode { regel.sjekkTilgang(vanligAnsatt.navId, vanligBruker.ident) }.doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.navId)
            bruker.bruker(vanligBruker.ident)
            midlertidig wasNot Called
        }
    }
    @Test
    @DisplayName("Test at sjekk om midlertidig tilgang er gitt gjøres om det kastes en exception fra en regel som er overstyrbar, om det er gitt midlertidig tilgang er sjekken OK")
    fun testMidlertidigGitt() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        every { bruker.bruker(geoUtlandBruker.ident) } returns geoUtlandBruker
        every { midlertidig.harOverstyrtTilgang(vanligAnsatt.navId, geoUtlandBruker.ident) } returns true
        assertThatCode {regel.sjekkTilgang(vanligAnsatt.navId, geoUtlandBruker.ident) }.doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.navId)
            bruker.bruker(geoUtlandBruker.ident)
            midlertidig.harOverstyrtTilgang(vanligAnsatt.navId, geoUtlandBruker.ident)
        }
    }
    @Test
    @DisplayName("Test at sjekk om midlertidig tilgang er gitt gjøres om det kastes en exception fra en regel som er overstyrbar, om det ikke er gitt midlertidig tilgang jastes feilen videre")
    fun testMidlertidigIkkeGitt() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        every { bruker.bruker(geoUtlandBruker.ident) } returns geoUtlandBruker
        every { midlertidig.harOverstyrtTilgang(vanligAnsatt.navId, geoUtlandBruker.ident) } returns false
        assertThrows<RegelException> {regel.sjekkTilgang(vanligAnsatt.navId, geoUtlandBruker.ident) }
        verify {
            ansatt.ansatt(vanligAnsatt.navId)
            bruker.bruker(geoUtlandBruker.ident)
            midlertidig.harOverstyrtTilgang(vanligAnsatt.navId, geoUtlandBruker.ident)
        }
    }

    @ParameterizedTest
    @MethodSource("kjerneregelProvider")
    @DisplayName("Test at exception kastet av en av kjernereglene kastes videre av error handler uten å sjekke midlertidig tilgang")
    fun ikkeOverstyrbar(regel: Regel)    {
        assertThrows<RegelException> {
            errorHandler.håndter(navid, fnr, RegelException(fnr,navid, regel))
        }
        verify {
            midlertidig wasNot Called
        }
    }

    companion object {
        @JvmStatic
        fun kjerneregelProvider(): Stream<Regel> = Stream.of(strengtFortroligRegel, fortroligRegel, egenAnsattRegel)
    }
}
