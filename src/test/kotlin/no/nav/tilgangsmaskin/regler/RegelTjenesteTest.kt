package no.nav.tilgangsmaskin.regler

import com.ninjasquad.springmockk.MockkBean
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Companion.utenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.TOMORROW
import no.nav.tilgangsmaskin.regler.ansatte.ansattId
import no.nav.tilgangsmaskin.regler.brukerids.fortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.strengtFortroligBrukerId
import no.nav.tilgangsmaskin.regler.brukerids.vanligBrukerId
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
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

    @Autowired
    private lateinit var repo: OverstyringRepository

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    @Autowired
    private lateinit var motor: RegelMotor

    @MockK
    private lateinit var bruker: BrukerTjeneste

    @MockK
    private lateinit var ansatt: AnsattTjeneste

    private lateinit var overstyring: OverstyringTjeneste

    private lateinit var regler: RegelTjeneste


    private lateinit var avdød: AvdødTeller
    private lateinit var egne: EgneDataOppslagTeller
    private lateinit var partner: PartnerOppslagTeller
    private lateinit var søsken: SøskenOppslagTeller
    private lateinit var foreldrebarn: ForeldreBarnOppslagTeller


    @BeforeTest
    fun before() {
        søsken = SøskenOppslagTeller(SimpleMeterRegistry(), accessor)
        foreldrebarn = ForeldreBarnOppslagTeller(SimpleMeterRegistry(), accessor)
        partner = PartnerOppslagTeller(SimpleMeterRegistry(), accessor)
        avdød = AvdødTeller(SimpleMeterRegistry(), accessor)
        egne = EgneDataOppslagTeller(SimpleMeterRegistry(), accessor)
        expect(AnsattBuilder().grupper(grupper.annenGruppe).build())
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        overstyring =
            OverstyringTjeneste(
                    ansatt,
                    bruker, OverstyringJPAAdapter(repo), motor,
                    SimpleMeterRegistry(), accessor)
        regler = RegelTjeneste(motor, bruker, ansatt, overstyring)
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        expect(BrukerBuilder(vanligBrukerId).build())
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "test", TOMORROW))
        assertThatCode {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()

    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        expect(BrukerBuilder(vanligBrukerId, utenlandskTilknytning).grupper(UTENLANDSK).build())
        assertThrows<RegelException> {
            regler.kompletteRegler(
                    AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                    BrukerBuilder(vanligBrukerId, utenlandskTilknytning).grupper(UTENLANDSK).build().brukerId.verdi)
        }
    }

    @Test
    fun bulkAvvisninger() {
        expect(AnsattBuilder().grupper(grupper.annenGruppe).build())
        every {
            bruker.brukere(setOf(strengtFortroligBrukerId.verdi, fortroligBrukerId.verdi))
        } returns listOf(
                BrukerBuilder(strengtFortroligBrukerId).grupper(STRENGT_FORTROLIG).build(),
                BrukerBuilder(fortroligBrukerId).grupper(FORTROLIG).build())
        assertEquals(assertThrows<BulkRegelException> {
            regler.bulkRegler(
                    AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                    setOf(
                            IdOgType(strengtFortroligBrukerId.verdi, KJERNE_REGELTYPE),
                            IdOgType(fortroligBrukerId.verdi, KJERNE_REGELTYPE)))
        }.exceptions.size, 2)
    }

    @Test
    fun bulkAvvisningerOverstyrt() {
        expect(
                BrukerBuilder(vanligBrukerId, utenlandskTilknytning).grupper(
                        UTENLANDSK).build())
        every {
            bruker.brukere(
                    setOf(
                            BrukerBuilder(
                                    vanligBrukerId,
                                    utenlandskTilknytning).grupper(
                                    UTENLANDSK).build().brukerId.verdi))
        } returns listOf(
                BrukerBuilder(
                        vanligBrukerId,
                        utenlandskTilknytning).grupper(
                        UTENLANDSK).build())
        overstyring.overstyr(
                AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId, OverstyringData(
                BrukerBuilder(vanligBrukerId, utenlandskTilknytning).grupper(
                        UTENLANDSK).build().brukerId,
                "test",
                LocalDate.now().plusDays(1)))
        assertThatCode {
            regler.bulkRegler(
                    AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
                    setOf(
                            IdOgType(
                                    BrukerBuilder(
                                            vanligBrukerId,
                                            utenlandskTilknytning).grupper(
                                            UTENLANDSK).build().brukerId.verdi, KOMPLETT_REGELTYPE)))
        }.doesNotThrowAnyException()
    }

    private fun expect(b: Bruker) {
        every {
            bruker.nærmesteFamilie(b.brukerId.verdi)
        } returns b
    }

    private fun expect(a: Ansatt) {
        every {
            ansatt.ansatt(a.ansattId)
        } returns a
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}