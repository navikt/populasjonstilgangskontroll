package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.brukere.vanligBrukerMedHistoriskIdent
import no.nav.tilgangsmaskin.regler.brukere.vanligHistoriskBruker
import no.nav.tilgangsmaskin.regler.brukerids
import no.nav.tilgangsmaskin.regler.grupper
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
    private lateinit var motor: RegelMotor

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    @MockK
    private lateinit var ansattTjeneste: AnsattTjeneste

    @MockK
    private lateinit var brukerTjeneste: BrukerTjeneste

    @Autowired
    private lateinit var repo: OverstyringRepository

    private lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        every {
            ansattTjeneste.ansatt(
                    AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId)
        } returns AnsattBuilder().grupper(
                grupper.annenGruppe)
            .build()
        overstyring = OverstyringTjeneste(
                ansattTjeneste, brukerTjeneste, OverstyringJPAAdapter(repo), motor,
                SimpleMeterRegistry(), accessor)
    }

    @Test
    @DisplayName("Test gyldig overstyring via historisk ident")
    fun testOverstyringGyldigHistorisk() {
        expect(vanligBrukerMedHistoriskIdent)
        expect(vanligHistoriskBruker)
        overstyring.overstyr(
                AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId, OverstyringData(
                vanligBrukerMedHistoriskIdent.historiskeIds.first(),
                "test",
                LocalDate.now().plusDays(1)))
        assertThat(
                overstyring.erOverstyrt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                        vanligBrukerMedHistoriskIdent.brukerId)).isTrue
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        expect(BrukerBuilder(brukerids.vanligBrukerId).build())
        overstyring.overstyr(
                AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId, OverstyringData(
                BrukerBuilder(brukerids.vanligBrukerId).build().brukerId,
                "gammel",
                LocalDate.now().minusDays(1)))
        overstyring.overstyr(
                AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId, OverstyringData(
                BrukerBuilder(brukerids.vanligBrukerId).build().brukerId,
                "ny",
                LocalDate.now().plusDays(1)))
        assertThat(
                overstyring.erOverstyrt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                        BrukerBuilder(brukerids.vanligBrukerId).build().brukerId)).isTrue
    }

    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyringUtgått() {
        expect(BrukerBuilder(brukerids.vanligBrukerId).build())
        overstyring.overstyr(
                AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId, OverstyringData(
                BrukerBuilder(brukerids.vanligBrukerId).build().brukerId,
                "ny",
                LocalDate.now().minusDays(1)))
        assertThat(
                overstyring.erOverstyrt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                        BrukerBuilder(brukerids.vanligBrukerId).build().brukerId)).isFalse

    }

    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringUtenDBInnslag() {
        expect(
                BrukerBuilder(brukerids.ukjentBostedBrukerId, GeografiskTilknytning.UkjentBosted())
                    .grupper(GlobalGruppe.UKJENT_BOSTED)
                    .build())
        assertThat(
                overstyring.erOverstyrt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                        BrukerBuilder(brukerids.ukjentBostedBrukerId, GeografiskTilknytning.UkjentBosted())
                            .grupper(GlobalGruppe.UKJENT_BOSTED)
                            .build().brukerId)).isFalse
    }

    private fun expect(bruker: Bruker) {
        every {
            brukerTjeneste.nærmesteFamilie(bruker.brukerId.verdi)
        } returns bruker
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}