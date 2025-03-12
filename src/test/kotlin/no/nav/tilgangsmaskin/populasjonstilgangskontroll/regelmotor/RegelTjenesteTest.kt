package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ansattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringSjekker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSpec
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste
    @MockK
    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste

    private lateinit var avvistHandler: OverstyringSjekker

    @BeforeEach
    fun before() {
        avvistHandler = OverstyringSjekker(overstyring)
        regel = RegelTjeneste(motor, bruker, ansatt,avvistHandler)
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
    }
    @Test
    @DisplayName("Verifiser at sjekk av overstyring ikke gjøres en regel som ikke er overstyrbar avslår tilgabg")
    fun testIngenOverstyringSjekk() {
        every { bruker.bruker(vanligBruker.brukerId) } returns vanligBruker
        assertThatCode { regel.kompletteRegler(vanligAnsatt.ansattId, vanligBruker.brukerId) }.doesNotThrowAnyException()
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
        assertThatCode { regel.kompletteRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }.doesNotThrowAnyException()
        verify {
            ansatt.ansatt(vanligAnsatt.ansattId)
            bruker.bruker(geoUtlandBruker.brukerId)
            overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId)
        }
    }
    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        every { overstyring.erOverstyrt(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) } returns false
        assertThrows<RegelException> { regel.kompletteRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }
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
            avvistHandler.sjekk(ansattId, vanligBrukerId, RegelException(vanligBrukerId, ansattId, regel))
        }
        verify {
            overstyring wasNot Called
        }
    }

    @Test
    fun bulk() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.bruker(strengtFortroligBruker.brukerId) } returns strengtFortroligBruker
        every { bruker.bruker(fortroligBruker.brukerId) } returns fortroligBruker
        every { bruker.bruker(vanligBruker.brukerId) } returns vanligBruker
        assertEquals(assertThrows<BulkRegelException> {
            regel.bulkRegler(vanligAnsatt.ansattId, listOf(RegelSpec(strengtFortroligBruker.brukerId, KJERNE), RegelSpec(fortroligBruker.brukerId, KJERNE)))
        }.exceptions.size, 2)
    }
    companion object {
        @JvmStatic
        fun kjerneregelProvider() = motor.kjerneRegelSett.regler.stream()
    }
}