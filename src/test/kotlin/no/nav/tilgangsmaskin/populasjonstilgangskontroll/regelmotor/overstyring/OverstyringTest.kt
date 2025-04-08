package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBrukerMedHistoriskIdent
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligHistoriskBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelBeanConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [RegelMotor::class,RegelBeanConfig::class,TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
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
        every { ansattTjeneste.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        overstyring = OverstyringTjeneste(ansattTjeneste, brukerTjeneste, OverstyringJPAAdapter(repo), motor,
            SimpleMeterRegistry()
        )
    }

    @Test
    @DisplayName("Test gyldig overstyring via historisk ident")
    fun testOverstyringGyldigHistorisk() {
        every { brukerTjeneste.bruker(vanligBrukerMedHistoriskIdent.brukerId) } returns vanligBrukerMedHistoriskIdent
        every { brukerTjeneste.bruker(vanligHistoriskBruker.brukerId) } returns vanligHistoriskBruker
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData(
            vanligBrukerMedHistoriskIdent.historiskeIdentifikatorer.first(),
            "test",
            LocalDate.now().plusDays(1)
        ))
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBrukerMedHistoriskIdent.brukerId)).isTrue
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        every { brukerTjeneste.bruker(vanligBruker.brukerId) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData(
            vanligBruker.brukerId,
            "gammel",
            LocalDate.now().minusDays(1)
        ))
        overstyring.overstyr(vanligAnsatt.ansattId,  OverstyringData(
            vanligBruker.brukerId,
            "ny",
            LocalDate.now().plusDays(1),))
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isTrue
    }
    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyringUtgått() {
        every { brukerTjeneste.bruker(vanligBruker.brukerId) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData(
            vanligBruker.brukerId,
            "ny",
            LocalDate.now().minusDays(1)
        ))
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isFalse

    }
    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringUtenDBInnslag() {
        every { brukerTjeneste.bruker(ukjentBostedBruker.brukerId) } returns ukjentBostedBruker
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, ukjentBostedBruker.brukerId)).isFalse
    }
}