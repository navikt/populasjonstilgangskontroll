package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.geoUtlandBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.strengtFortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.fortroligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {

    @Autowired
    lateinit var repo: OverstyringRepository

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor
    @MockkBean
    lateinit var avdød : AvdødOppslagTeller
    @Autowired
    lateinit var motor: RegelMotor
    @MockK
    private lateinit var bruker: BrukerTjeneste
    @MockK
    private lateinit var ansatt: AnsattTjeneste

    lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste


    @BeforeTest
    fun before() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        overstyring = OverstyringTjeneste(ansatt, bruker, OverstyringJPAAdapter(repo), motor, SimpleMeterRegistry(), accessor)
        regel = RegelTjeneste(motor, bruker, ansatt,overstyring)
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData(
            geoUtlandBruker.brukerId,
            "test",
            LocalDate.now().plusDays(1)
        ))
        assertThatCode { regel.kompletteRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }.doesNotThrowAnyException()
    }
    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        assertThrows<RegelException> { regel.kompletteRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId) }
    }

    @Test
    fun bulkAvvisninger() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.bruker(strengtFortroligBruker.brukerId) } returns strengtFortroligBruker
        every { bruker.bruker(fortroligBruker.brukerId) } returns fortroligBruker
        every { bruker.bruker(vanligBruker.brukerId) } returns vanligBruker
        every { bruker.brukere(listOf(strengtFortroligBruker.brukerId,fortroligBruker.brukerId)) } returns listOf(strengtFortroligBruker,fortroligBruker)
        assertEquals(assertThrows<BulkRegelException> {
            regel.bulkRegler(vanligAnsatt.ansattId, listOf(IdOgType(strengtFortroligBruker.brukerId, KJERNE_REGELTYPE), IdOgType(fortroligBruker.brukerId, KJERNE_REGELTYPE)))
        }.exceptions.size, 2)
    }
    @Test
    fun bulkAvvisningerOverstyrt() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.bruker(geoUtlandBruker.brukerId) } returns geoUtlandBruker
        every { bruker.brukere(listOf(geoUtlandBruker.brukerId)) } returns listOf(geoUtlandBruker)
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData(
            geoUtlandBruker.brukerId,
            "test",
            LocalDate.now().plusDays(1)
        ))
        assertThatCode {
            regel.bulkRegler(vanligAnsatt.ansattId, listOf(IdOgType(geoUtlandBruker.brukerId,KOMPLETT_REGELTYPE)))
        }.doesNotThrowAnyException()
    }
}