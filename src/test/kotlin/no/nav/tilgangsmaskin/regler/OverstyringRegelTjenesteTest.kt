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
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.regler.motor.*
import no.nav.tilgangsmaskin.regler.overstyring.*
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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest

@Import(RegelTestConfig::class)
@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, Auditor::class])
@ExtendWith(MockKExtension::class)
@AutoConfigureMetrics
@Testcontainers
@ActiveProfiles(TEST)
class OverstyringRegelTjenesteTest {

    private val strengtFortroligAktørId = AktørId("1234567890123")
    private val strengtFortroligBrukerId = BrukerId("08526835671")
    private val fortroligBrukerId = BrukerId("08526835672")
    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val dnr = BrukerId("12345678910")

    @Autowired
    private lateinit var registry: MeterRegistry
    @Autowired
    private lateinit var repo: OverstyringRepository
    @MockkBean
    private lateinit var token: Token
    @MockkBean
    private lateinit var oppfølging: OppfølgingTjeneste
    @MockkBean
    private lateinit var validator: OverstyringClientValidator
    @MockkBean
    private lateinit var proxy: EntraProxyTjeneste
    @Autowired
    private lateinit var motor: RegelMotor
    @MockK
    private lateinit var brukere: BrukerTjeneste
    @MockK
    private lateinit var ansatte: AnsattTjeneste
    private lateinit var overstyring: OverstyringTjeneste
    private lateinit var regler: RegelTjeneste

    @BeforeTest
    fun before() {
        every { validator.validerKonsument() } returns Unit
        every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "Testenhet")
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
        every { oppfølging.enhetFor(Identifikator(vanligBrukerId.verdi)) } returns Enhetsnummer("1234")
        every { token.system } returns "test"
        every { token.ansattId } returns ansattId
        every { token.clusterAndSystem } returns "cluster:test"
        every { token.systemNavn } returns "test"
        every { token.erObo } returns false
        every { token.erCC } returns true
        overstyring = OverstyringTjeneste(ansatte, brukere, OverstyringJPAAdapter(repo), motor, proxy, validator, OverstyringTeller(registry, token))
        regler = RegelTjeneste(motor, brukere, ansatte, overstyring)
    }

    @Test
    @DisplayName("Verifiser at brukere uten tilgang havner i avviste i bulk")
    fun bulkAvvisninger() {
        every {
            brukere.brukere(setOf(strengtFortroligAktørId.verdi, fortroligBrukerId.verdi))
        } returns setOf(
            BrukerBuilder(strengtFortroligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).oppslagId(strengtFortroligAktørId.verdi).aktørId(strengtFortroligAktørId).build(),
            BrukerBuilder(fortroligBrukerId).kreverMedlemskapI(FORTROLIG).build())

        val resultater = regler.bulkRegler(ansattId,
            setOf(BrukerIdOgRegelsett(strengtFortroligAktørId.verdi), BrukerIdOgRegelsett(fortroligBrukerId.verdi)))

        assertThat(resultater.avviste.map { it.brukerId }.containsAll(listOf(strengtFortroligAktørId.verdi, fortroligBrukerId.verdi)))
        assertThat(resultater.avviste).hasSize(2)
        assertThat(resultater.godkjente).isEmpty()
        assertThat(resultater.ukjente).isEmpty()
    }

    @Test
    @DisplayName("Verifiser at et dnr som senere har blitt erstattet med et fnr, ikke avvises")
    fun dnr() {
        every {
            brukere.brukerMedNærmesteFamilie(dnr.verdi)
        } returns BrukerBuilder(vanligBrukerId).historiske(setOf(dnr)).build()

        assertThatCode {
            regler.kompletteRegler(ansattId, dnr.verdi)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at et dnr som senere har blitt erstattet med et fnr, ikke avvises i bulk")
    fun dnrBulk() {
        every {
            brukere.brukere(setOf(dnr.verdi))
        } returns setOf(BrukerBuilder(vanligBrukerId).oppslagId(dnr.verdi).historiske(setOf(dnr)).build())

        val resultat = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(dnr.verdi)))

        assertThat(resultat.godkjente.isNotEmpty())
    }

    @Test
    @DisplayName("Verifiser at kjerneregler kjøres uten unntak når bruker finnes i PDL")
    fun kjernereglerHappyPath() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId).build()

        assertThatCode {
            regler.kjerneregler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at overstyring ikke registreres når kjerneregler avslår")
    fun overstyringFeilerNårKjernereglerAvslår() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()

        assertThrows<RegelException> {
            overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
        }
        assertThat(overstyring.erOverstyrt(ansattId, vanligBrukerId)).isFalse
    }

    @Test
    @DisplayName("Verifiser at tilgang gis om overstyring er registrert og en regel avslår")
    fun overstyringOK() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId).build()
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))

        assertThatCode {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at tilgang ikke gis når regel avslår og ingen overstyring er registrert")
    fun ikkeOverstyrt() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()

        assertThrows<RegelException> {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }
    }

    @Test
    @DisplayName("Verifiser at avvist bruker i bulk godkjennes når overstyring er registrert")
    fun bulkAvvisningerOverstyrt() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
        every {
            brukere.brukere(setOf(vanligBrukerId.verdi))
        } returns setOf(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build())
        overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er en test", IMORGEN))

        val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))

        assertThat(resultater.avviste).isEmpty()
        assertThat(resultater.godkjente).hasSize(1)
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
    }
}




