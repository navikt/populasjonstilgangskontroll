package no.nav.tilgangsmaskin.regler

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
import no.nav.tilgangsmaskin.regler.ansatte.vanligAnsatt
import no.nav.tilgangsmaskin.regler.brukere.fortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.geoUtlandBruker
import no.nav.tilgangsmaskin.regler.brukere.strengtFortroligBruker
import no.nav.tilgangsmaskin.regler.brukere.vanligBruker
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
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@Import(RegelConfig::class)
@ActiveProfiles(TEST)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(Grupper::class)
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
@AutoConfigureObservability
class RegelTjenesteTest {

    @Autowired
    lateinit var repo: OverstyringRepository

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor

    @Autowired
    lateinit var motor: RegelMotor

    @MockK
    private lateinit var bruker: BrukerTjeneste

    @MockK
    private lateinit var ansatt: AnsattTjeneste

    lateinit var overstyring: OverstyringTjeneste

    private lateinit var regel: RegelTjeneste


    lateinit var avdød: AvdødTeller
    lateinit var egne: EgneDataOppslagTeller
    lateinit var partner: PartnerOppslagTeller
    lateinit var søsken: SøskenOppslagTeller
    lateinit var foreldrebarn: ForeldreBarnOppslagTeller


    @BeforeTest
    fun before() {
        søsken = SøskenOppslagTeller(SimpleMeterRegistry(), accessor)
        foreldrebarn = ForeldreBarnOppslagTeller(SimpleMeterRegistry(), accessor)
        partner = PartnerOppslagTeller(SimpleMeterRegistry(), accessor)
        avdød = AvdødTeller(SimpleMeterRegistry(), accessor)
        egne = EgneDataOppslagTeller(SimpleMeterRegistry(), accessor)
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { accessor.system } returns "test"
        every { accessor.systemNavn } returns "test"
        overstyring =
            OverstyringTjeneste(
                    ansatt,
                    bruker, OverstyringJPAAdapter(repo),
                    motor,
                    SimpleMeterRegistry(),
                    accessor
                               )
        regel = RegelTjeneste(motor, bruker, ansatt, overstyring)
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang, og at tilgang gis om overstyring er gjort")
    fun overstyringOK() {
        every { bruker.utvidetFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        every { bruker.nærmesteFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                geoUtlandBruker.brukerId,
                "test",
                LocalDate.now().plusDays(1)
                                                      )
                            )
        assertThatCode {
            regel.kompletteRegler(
                    vanligAnsatt.ansattId,
                    geoUtlandBruker.brukerId.verdi
                                 )
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at sjekk om overstyring  gjøres om en regel som er overstyrbar avslår tilgang,og at tilgang ikke gis om overstyring ikke er gjort")
    fun ikkeOverstyrt() {
        every { bruker.nærmesteFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        every { bruker.utvidetFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        assertThrows<RegelException> { regel.kompletteRegler(vanligAnsatt.ansattId, geoUtlandBruker.brukerId.verdi) }
    }

    @Test
    fun bulkAvvisninger() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.utvidetFamilie(strengtFortroligBruker.brukerId.verdi) } returns strengtFortroligBruker
        every { bruker.utvidetFamilie(fortroligBruker.brukerId.verdi) } returns fortroligBruker
        every { bruker.utvidetFamilie(vanligBruker.brukerId.verdi) } returns vanligBruker
        every {
            bruker.brukere(
                    setOf(
                            strengtFortroligBruker.brukerId.verdi,
                            fortroligBruker.brukerId.verdi
                         )
                          )
        } returns listOf(strengtFortroligBruker, fortroligBruker)
        assertEquals(assertThrows<BulkRegelException> {
            regel.bulkRegler(
                    vanligAnsatt.ansattId,
                    listOf(
                            IdOgType(strengtFortroligBruker.brukerId.verdi, KJERNE_REGELTYPE),
                            IdOgType(fortroligBruker.brukerId.verdi, KJERNE_REGELTYPE)
                          )
                            )
        }.exceptions.size, 2)
    }

    @Test
    fun bulkAvvisningerOverstyrt() {
        every { ansatt.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        every { bruker.nærmesteFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        every { bruker.utvidetFamilie(geoUtlandBruker.brukerId.verdi) } returns geoUtlandBruker
        every { bruker.brukere(setOf(geoUtlandBruker.brukerId.verdi)) } returns listOf(geoUtlandBruker)
        overstyring.overstyr(
                vanligAnsatt.ansattId, OverstyringData(
                geoUtlandBruker.brukerId,
                "test",
                LocalDate.now().plusDays(1)
                                                      )
                            )
        assertThatCode {
            regel.bulkRegler(
                    vanligAnsatt.ansattId,
                    listOf(IdOgType(geoUtlandBruker.brukerId.verdi, KOMPLETT_REGELTYPE))
                            )
        }.doesNotThrowAnyException()
    }
}