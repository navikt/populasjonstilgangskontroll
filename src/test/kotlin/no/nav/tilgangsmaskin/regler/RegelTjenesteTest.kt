package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.utenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.TOMORROW
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringJPAAdapter
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringRepository
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@Import(RegelConfig::class)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(Grupper::class)
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
@AutoConfigureObservability
@Testcontainers
class RegelTjenesteTest {

    private val strengtFortroligBrukerId = BrukerId("08526835671")
    private val fortroligBrukerId = BrukerId("08526835672")
    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")


    @Autowired
    private lateinit var repo: OverstyringRepository

    @Autowired
    private lateinit var registry: MeterRegistry

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    @Autowired
    private lateinit var motor: RegelMotor

    @MockK
    private lateinit var brukere: BrukerTjeneste

    @MockK
    private lateinit var ansatte: AnsattTjeneste

    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regler: RegelTjeneste


    private lateinit var avdød: AvdødTeller
    private lateinit var egne: EgneDataOppslagTeller
    private lateinit var partner: PartnerOppslagTeller
    private lateinit var søsken: SøskenOppslagTeller
    private lateinit var foreldrebarn: ForeldreBarnOppslagTeller

    @BeforeTest
    fun before() {
        søsken = SøskenOppslagTeller(registry, accessor)
        foreldrebarn = ForeldreBarnOppslagTeller(registry, accessor)
        partner = PartnerOppslagTeller(registry, accessor)
        avdød = AvdødTeller(registry, accessor)
        egne = EgneDataOppslagTeller(registry, accessor)
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder().build()
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        overstyring =
            OverstyringTjeneste(
                    ansatte, brukere, OverstyringJPAAdapter(repo), motor,
                    registry, accessor)
        regler = RegelTjeneste(motor, brukere, ansatte, overstyring)
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "test", TOMORROW))
        assertThatCode {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every {
            brukere.nærmesteFamilie(
                    BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK)
                        .build().brukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK).build()
        assertThrows<RegelException> {
            val brukerId =
                BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK).build().brukerId
            regler.kompletteRegler(ansattId, brukerId.verdi)
        }
    }

    @Test
    fun bulkAvvisninger() {
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder().build()
        every {
            brukere.brukere(strengtFortroligBrukerId.verdi, fortroligBrukerId.verdi)
        } returns listOf(
                BrukerBuilder(strengtFortroligBrukerId).krever(STRENGT_FORTROLIG).build(),
                BrukerBuilder(fortroligBrukerId).krever(FORTROLIG).build())
        assertEquals(assertThrows<BulkRegelException> {
            regler.bulkRegler(
                    ansattId,
                    setOf(IdOgType(strengtFortroligBrukerId.verdi), IdOgType(fortroligBrukerId.verdi)))
        }.exceptions.size, 2)
    }

    @Test
    fun bulkAvvisningerOverstyrt() {
        every {
            brukere.nærmesteFamilie(
                    BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(
                            UTENLANDSK).build().brukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK).build()
        every {
            brukere.brukere(
                    BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK)
                        .build().brukerId.verdi)
        } returns listOf(
                BrukerBuilder(
                        vanligBrukerId,
                        utenlandskTilknytning).krever(
                        UTENLANDSK).build())
        overstyring.overstyr(
                ansattId, OverstyringData(
                BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(
                        UTENLANDSK).build().brukerId,
                "test",
                TOMORROW))
        assertThatCode {
            regler.bulkRegler(
                    ansattId,
                    setOf(
                            IdOgType(
                                    BrukerBuilder(vanligBrukerId, utenlandskTilknytning).krever(UTENLANDSK)
                                        .build().brukerId.verdi)))
        }.doesNotThrowAnyException()
    }

    companion object {


        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}