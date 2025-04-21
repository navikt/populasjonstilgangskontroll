package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.regler.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.regler.TestData.vanligBruker
import no.nav.tilgangsmaskin.regler.TestData.vanligBrukerMedHistoriskIdent
import no.nav.tilgangsmaskin.regler.TestData.vanligHistoriskBruker
import no.nav.tilgangsmaskin.regler.motor.RegelBeanConfig
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
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
internal class OverstyringTest {

    @Autowired
    lateinit var motor: RegelMotor

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor

    @MockK
    lateinit var ansattTjeneste: AnsattTjeneste

    @MockK
    lateinit var brukerTjeneste: BrukerTjeneste

    @Autowired
    lateinit var repo: OverstyringRepository

    lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        every { ansattTjeneste.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        overstyring = OverstyringTjeneste(
                ansattTjeneste, brukerTjeneste, OverstyringJPAAdapter(repo), motor,
                SimpleMeterRegistry(), accessor
                                         )
    }

    @Test
    @DisplayName("Test gyldig overstyring via historisk ident")
    fun testOverstyringGyldigHistorisk() {
        every { brukerTjeneste.nærmesteFamilie(vanligBrukerMedHistoriskIdent.brukerId.verdi) } returns vanligBrukerMedHistoriskIdent
        every { brukerTjeneste.utvidetFamilie(vanligBrukerMedHistoriskIdent.brukerId.verdi) } returns vanligBrukerMedHistoriskIdent
        every { brukerTjeneste.utvidetFamilie(vanligHistoriskBruker.brukerId.verdi) } returns vanligHistoriskBruker
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                vanligBrukerMedHistoriskIdent.historiskeIdentifikatorer.first(),
                "test",
                LocalDate.now().plusDays(1)
                                                      )
                            )
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBrukerMedHistoriskIdent.brukerId)).isTrue
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        every { brukerTjeneste.nærmesteFamilie(vanligBruker.brukerId.verdi) } returns vanligBruker
        every { brukerTjeneste.utvidetFamilie(vanligBruker.brukerId.verdi) } returns vanligBruker
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                vanligBruker.brukerId,
                "gammel",
                LocalDate.now().minusDays(1)
                                                      )
                            )
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                vanligBruker.brukerId,
                "ny",
                LocalDate.now().plusDays(1),
                                                      )
                            )
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isTrue
    }

    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyringUtgått() {
        every { brukerTjeneste.utvidetFamilie(vanligBruker.brukerId.verdi) } returns vanligBruker
        every { brukerTjeneste.nærmesteFamilie(vanligBruker.brukerId.verdi) } returns vanligBruker
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                vanligBruker.brukerId,
                "ny",
                LocalDate.now().minusDays(1)
                                                      )
                            )
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isFalse

    }

    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringUtenDBInnslag() {
        every { brukerTjeneste.nærmesteFamilie(ukjentBostedBruker.brukerId.verdi) } returns ukjentBostedBruker
        every { brukerTjeneste.utvidetFamilie(ukjentBostedBruker.brukerId.verdi) } returns ukjentBostedBruker
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, ukjentBostedBruker.brukerId)).isFalse
    }

    companion object {
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:17")
    }
}