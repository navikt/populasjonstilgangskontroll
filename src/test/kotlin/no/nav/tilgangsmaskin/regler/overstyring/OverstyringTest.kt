package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.udefinertTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.TOMORROW
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.YESTERDAY
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.ansatte.ansattId
import no.nav.tilgangsmaskin.regler.brukerids.ukjentBostedBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.vanligBrukerId
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
@ContextConfiguration(classes = [RegelMotor::class, RegelBeanConfig::class, TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@AutoConfigureObservability
internal class OverstyringTest {

    private val historiskBrukerId = BrukerId("11111111111")

    @Autowired
    private lateinit var motor: RegelMotor

    @Autowired
    private lateinit var registry: MeterRegistry

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    @MockK
    private lateinit var ansatte: AnsattTjeneste

    @MockK
    private lateinit var brukere: BrukerTjeneste

    @Autowired
    private lateinit var repo: OverstyringRepository

    private lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder().build()
        overstyring = OverstyringTjeneste(ansatte, brukere, OverstyringJPAAdapter(repo), motor, registry, accessor)
    }

    @Test
    @DisplayName("Test gyldig overstyring via historisk ident")
    fun testOverstyringGyldigHistorisk() {
        every {
            brukere.nærmesteFamilie(
                    BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build().brukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build()
        every {
            brukere.nærmesteFamilie(
                    BrukerBuilder(historiskBrukerId)
                        .gt(udefinertTilknytning)
                        .build().brukerId.verdi)
        } returns BrukerBuilder(historiskBrukerId).gt(udefinertTilknytning).build()
        overstyring.overstyr(
                ansattId, OverstyringData(
                BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId))
                    .build().historiskeIds.first(), "test", TOMORROW))
        assertThat(
                overstyring.erOverstyrt(
                        ansattId, BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId))
                    .build().brukerId)).isTrue
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "gammel", YESTERDAY))
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "ny", TOMORROW))
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isTrue
    }

    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyringUtgått() {
        every { brukere.nærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
        overstyring.overstyr(
                ansattId, OverstyringData(vanligBrukerId, "ny", YESTERDAY))
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isFalse

    }

    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringUtenDBInnslag() {
        val bruker = BrukerBuilder(ukjentBostedBrukerId, UkjentBosted())
            .grupper(UKJENT_BOSTED)
            .build()
        every { brukere.nærmesteFamilie(ukjentBostedBrukerId.verdi) } returns bruker
        assertThat(overstyring.erOverstyrt(ansattId, ukjentBostedBrukerId)).isFalse
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}