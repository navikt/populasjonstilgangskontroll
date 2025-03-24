package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
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

    @Autowired
    lateinit var repo: OverstyringRepository

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

    lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste


    @BeforeTest
    fun before() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { accessor.system } returns "test"
        overstyring = OverstyringTjeneste(ansatt, bruker, OverstyringJPAAdapter(repo), motor)
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