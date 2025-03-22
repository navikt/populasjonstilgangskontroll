package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringSjekker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@RestClientTest
@TestPropertySource(properties = [
    "gruppe.strengt=5ef775f2-61f8-4283-bf3d-8d03f428aa14",
    "gruppe.nasjonal=c7107487-310d-4c06-83e0-cf5395dc3be3",
    "gruppe.utland=de62a4bf-957b-4cde-acdb-6d8bcbf821a0",
    "gruppe.udefinert=35d9d1ac-7fcb-4a22-9155-e0d1e57898a8",
    "gruppe.fortrolig=ea930b6b-9397-44d9-b9e6-f4cf527a632a",
    "gruppe.egenansatt=dbe4ad45-320b-4e9a-aaa1-73cca4ee124d"])
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor
    @MockkBean
    lateinit var meterRegistry : MeterRegistry
    @Autowired
    lateinit var motor: RegelMotor
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste
    @MockK
    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste

    private lateinit var sjekker: OverstyringSjekker

    @BeforeEach
    fun before() {
        sjekker = OverstyringSjekker(overstyring)
        regel = RegelTjeneste(motor, bruker, ansatt,sjekker)
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

    /*
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
    }*/

    @Test
    fun bulk() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.bruker(strengtFortroligBruker.brukerId) } returns strengtFortroligBruker
        every { bruker.bruker(fortroligBruker.brukerId) } returns fortroligBruker
        every { bruker.bruker(vanligBruker.brukerId) } returns vanligBruker
        assertEquals(assertThrows<BulkRegelException> {
            regel.bulkRegler(vanligAnsatt.ansattId, listOf(RegelSpec(strengtFortroligBruker.brukerId, KJERNE_REGELTYPE), RegelSpec(fortroligBruker.brukerId, KJERNE_REGELTYPE)))
        }.exceptions.size, 2)
    }

    /*
    companion object {
        @JvmStatic
        fun kjerneregelProvider() = motor.kjerneRegelSett.regler.stream()
    }
*/
}