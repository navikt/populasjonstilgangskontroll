package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.brukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.ansattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste
    @MockK
    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste

    private lateinit var avvistHandler: TilgangAvvistHandler

    @BeforeEach
    fun before() {
        avvistHandler = TilgangAvvistHandler(overstyring)
        regel = RegelTjeneste(motor, bruker, ansatt,avvistHandler)
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
    }
    @Test
    @DisplayName("Verifiser at sjekk av overstyring ikke gjøres en regel som ikke er overstyrbar avslår tilgabg")
    fun testIngenOverstyringSjekk() {
        every { bruker.bruker(vanligBruker.brukerId) } returns vanligBruker
        assertThatCode { regel.alleRegler(vanligAnsatt.ansattId, vanligBruker.brukerId) }.doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.ansattId)
            bruker.bruker(vanligBruker.brukerId)
            overstyring wasNot Called
        }
    }
    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        every { overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) } returns true
        assertThatCode { regel.alleRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }.doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.ansattId)
            bruker.bruker(geoUtlandBruker.brukerId)
            overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId)
        }
    }
    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        every { overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) } returns false
        assertThrows<RegelException> { regel.alleRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }
        verify {
            ansatt.ansatt(vanligAnsatt.ansattId)
            bruker.bruker(geoUtlandBruker.brukerId)
            overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId)
        }
    }

    @ParameterizedTest
    @MethodSource("kjerneregelProvider")
    @DisplayName("Test at tilgang avvist av en av kjernereglene ikke fører til sjekk av midlertidig tilgang")
    fun ikkeOverstyrbar(regel: Regel)    {
        assertThrows<RegelException> {
            avvistHandler.håndter(ansattId, brukerId, RegelException(brukerId, ansattId, regel))
        }
        verify {
            overstyring wasNot Called
        }
    }

    companion object {
        @JvmStatic
        fun kjerneregelProvider() = motor.kjerneregler.stream()
    }
}