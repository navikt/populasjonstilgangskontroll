package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOperations
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.TOMORROW
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.YESTERDAY
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.RegelBeanConfig
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [RegelMotor::class, OverstyringJPAAdapter::class, RegelBeanConfig::class, TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@AutoConfigureObservability
internal class OverstyringTest {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val historiskBrukerId = BrukerId("11111111111")

    @Autowired
    private lateinit var motor: RegelMotor

    @Autowired
    private lateinit var registry: MeterRegistry

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    @MockK
    private lateinit var ansatte: AnsattOperations

    @MockK
    private lateinit var brukere: BrukerTjeneste

    @Autowired
    private lateinit var adapter: OverstyringJPAAdapter

    private lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
        overstyring = OverstyringTjeneste(ansatte, brukere, adapter, motor, registry, accessor)
    }

    @Test
    @DisplayName("Gyldig overstyring via historisk ident")
    fun testOverstyringGyldigHistorisk() {
        val brukerMedHistorikk = BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build()
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns brukerMedHistorikk
        every { brukere.nærmesteFamilie(historiskBrukerId.verdi) } returns BrukerBuilder(historiskBrukerId).build()
        overstyring.overstyr(ansattId, OverstyringData(historiskBrukerId, "test", TOMORROW))
        assertThat(overstyring.erOverstyrt(ansattId, BrukerBuilder(vanligBrukerId).build().brukerId)).isTrue
    }

    @Test
    @DisplayName("Gyldig overstyring")
    fun testOverstyringGyldig() {
        val bruker = BrukerBuilder(vanligBrukerId).build()
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns bruker
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "gammel", YESTERDAY))
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "ny", TOMORROW))
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isTrue
    }

    @Test
    @DisplayName("Utgått overstyring")
    fun testOverstyringUtgått() {
        val bruker = BrukerBuilder(vanligBrukerId).build()
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns bruker
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "ny", YESTERDAY))
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isFalse
    }

    @Test
    @DisplayName("Overstyring uten db innslag")
    fun testOverstyringUtenDBInnslag() {
        val bruker = BrukerBuilder(vanligBrukerId).build()
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns bruker
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isFalse
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}