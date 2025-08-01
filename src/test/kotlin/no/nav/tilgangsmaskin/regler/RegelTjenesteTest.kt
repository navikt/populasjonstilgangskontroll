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
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.overstyring.*
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest

@Import(RegelTestConfig::class)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value= [RegelConfig::class, GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
@AutoConfigureObservability
@Testcontainers
@ActiveProfiles(TEST)
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
    private lateinit var token: Token

    @Autowired
    private lateinit var motor: RegelMotor

    @MockK
    private lateinit var brukere: BrukerTjeneste

    @MockK
    private lateinit var ansatte: AnsattTjeneste

    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regler: RegelTjeneste

    private lateinit var avvisningTeller : AvvisningTeller
    private lateinit var avdød: AvdødTeller


    @BeforeTest
    fun before() {
        avvisningTeller = AvvisningTeller(registry, token)
        avdød = AvdødTeller(registry, token)
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
        every { token.system } returns "test"
        every { token.systemNavn } returns "test"
        overstyring = OverstyringTjeneste(ansatte, brukere, OverstyringJPAAdapter(repo), motor, OverstyringTeller(registry, token))
        regler = RegelTjeneste(motor, brukere, ansatte, overstyring)
    }

    //@Test
    @DisplayName("Verifiser at sjekk om overstyring gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
        assertThatCode {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }

    //@Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every {
            brukere.brukerMedNærmesteFamilie(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build().brukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
        assertThrows<RegelException> {
            val brukerId = BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build().brukerId
            regler.kompletteRegler(ansattId, brukerId.verdi)
        }
    }

    @Test
    fun bulkAvvisninger() {
        every {
            brukere.brukerMedNærmesteFamilie(fortroligBrukerId.verdi)
        } returns BrukerBuilder(fortroligBrukerId).build()

        every {
            brukere.brukerMedNærmesteFamilie(strengtFortroligBrukerId.verdi)
        } returns BrukerBuilder(strengtFortroligBrukerId).build()

        every {
            brukere.brukere(setOf(strengtFortroligBrukerId.verdi, fortroligBrukerId.verdi))
        } returns setOf(BrukerBuilder(strengtFortroligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build(),
                BrukerBuilder(fortroligBrukerId).kreverMedlemskapI(FORTROLIG).build())

        val resultater = regler.bulkRegler(ansattId,
                    setOf(BrukerIdOgRegelsett(strengtFortroligBrukerId), BrukerIdOgRegelsett(fortroligBrukerId)))
        assertThat(resultater.avviste).hasSize(2)
        assertThat(resultater.godkjente).isEmpty()
        assertThat(resultater.ukjente).isEmpty()
    }

    @Test
    fun bulkAvvisningerOverstyrt() {
        every {
            brukere.brukerMedNærmesteFamilie(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build().brukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
        every {
            brukere.brukere(setOf(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build().brukerId.verdi))
        } returns setOf(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build())

        overstyring.overstyr(ansattId, OverstyringData(
                BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(
                    UTENLANDSK).build().brukerId, "Dette er en test", IMORGEN))
        val resultater  =  regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build().brukerId)))

        assertThat(resultater.avviste.isEmpty())
        assertThat(resultater.godkjente.isEmpty())
        assertThat(resultater.godkjente).hasSize(1)
    }

    companion object {


        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}