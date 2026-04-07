package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.RegelTestConfig
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringClientValidator.OverstyringException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.tilgang.Token
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
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Import(RegelTestConfig::class)
@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@AutoConfigureMetrics
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, LocalAuditor::class,OverstyringJPAAdapter::class])
@ApplyExtension(SpringExtension::class)
internal class OverstyringTest : DescribeSpec() {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val historiskBrukerId = BrukerId("11111111111")


    @MockkBean
    private lateinit var vergemål: VergemålTjeneste

    @MockkBean
    private lateinit var nom: NomTjeneste

    @MockkBean
    lateinit var validator: OverstyringClientValidator
    @MockkBean
    lateinit var proxy: EntraProxyTjeneste
    @MockkBean
    lateinit var token: Token
    @MockkBean
    lateinit var oppfølging: OppfølgingTjeneste
    @Autowired
    lateinit var motor: RegelMotor
    @Autowired
    lateinit var registry: MeterRegistry
    @Autowired lateinit var adapter: OverstyringJPAAdapter
    @Autowired lateinit var repository: OverstyringRepository
    @MockK
    lateinit var ansatte: AnsattTjeneste
    @MockK
    lateinit var brukere: BrukerTjeneste

    init {
        lateinit var overstyring: OverstyringTjeneste

        beforeSpec {
            MockKAnnotations.init(this@OverstyringTest)
        }

        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns vanligBrukerId
            every { vergemål.vergemål(any()) } returns emptyList()  
            every { validator.validerKonsument() } returns Unit
            every { token.erObo } returns false
            every { token.erCC } returns true
            every { token.system } returns "test"
            every { token.ansattId } returns ansattId
            every { token.systemNavn } returns "test"
            every { token.clusterAndSystem } returns "cluster:test"
            every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "Testenhet")
            every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
            overstyring = OverstyringTjeneste(ansatte, brukere, adapter, motor, proxy, validator, OverstyringTeller(registry, token))
        }

        describe("overstyr") {

            it("exception som ikke er RegelException kastes videre") {
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } throws RuntimeException("teknisk feil")

                shouldThrow<RuntimeException> {
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
                }
            }

            it("OverstyringKlientException kastes videre") {
                every { validator.validerKonsument() } throws OverstyringException("ukjent system", "ukjent-system")

                shouldThrow<OverstyringException> {
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
                }
            }
        }

        describe("OverstyringEntity felter") {

            it("alle felter settes når overstyring registreres") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))

                val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                assertSoftly(entity) {
                    navid shouldBe ansattId.verdi
                    fnr shouldBe vanligBrukerId.verdi
                    begrunnelse shouldBe "Dette er en begrunnelse"
                    enhet shouldBe "1234"
                    expires shouldNotBe null
                    id shouldNotBe 0
                    created shouldNotBe null
                    updated shouldNotBe null
                    oppretter shouldBe ansattId.verdi
                    system shouldBe "test"
                }
            }
        }

        describe("erOverstyrt") {

            it("gyldig overstyring via historisk ident") {
                val brukerMedHistorikk = BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns brukerMedHistorikk
                every { brukere.brukerMedNærmesteFamilie(historiskBrukerId.verdi) } returns BrukerBuilder(historiskBrukerId).build()

                overstyring.overstyr(ansattId, OverstyringData(historiskBrukerId, "Dette er en test", IMORGEN))

                overstyring.erOverstyrt(ansattId, BrukerBuilder(vanligBrukerId).build().brukerId) shouldBe true
            }

            it("nyeste overstyring gjelder når det finnes flere") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Denne er gammel", IGÅR))
                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Denne er ny", IMORGEN))

                overstyring.erOverstyrt(ansattId, bruker.brukerId) shouldBe true
            }

            it("utgått overstyring gir ikke tilgang") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Denne er utgått", IGÅR))

                overstyring.erOverstyrt(ansattId, bruker.brukerId) shouldBe false
            }

            it("returnerer false når ingen overstyring er registrert") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.erOverstyrt(ansattId, bruker.brukerId) shouldBe false
            }
        }

        describe("OverstyringEntityListener") {

            it("setter created, updated, oppretter og system ved @PrePersist") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))

                val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                assertSoftly(entity) {
                    created shouldNotBe null
                    updated shouldNotBe null
                    created shouldBe updated
                    oppretter shouldBe ansattId.verdi
                    system shouldBe "test"
                }
            }

            it("laster entity med korrekte felter fra database ved @PostLoad") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))

                val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                val lastet = repository.findById(entity.id)

                lastet.isPresent shouldBe true
                with(lastet.get()) {
                    navid shouldBe ansattId.verdi
                    fnr shouldBe vanligBrukerId.verdi
                    created shouldNotBe null
                    updated shouldNotBe null
                    oppretter shouldBe ansattId.verdi
                    system shouldBe "test"
                }
            }

            it("nullstiller system og oppretter til tokenverdi ved @PreUpdate") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))

                val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                val createdFør = entity.created
                entity.system = "ukjent-system"
                entity.oppretter = "X000000"
                repository.saveAndFlush(entity)

                val oppdatert = repository.findById(entity.id).get()
                assertSoftly(oppdatert) {
                    system shouldBe "test"
                    oppretter shouldBe ansattId.verdi
                    created shouldBe createdFør
                }
            }

            it("fjerner entity fra database ved @PreRemove og @PostRemove") {
                val bruker = BrukerBuilder(vanligBrukerId).build()
                every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker

                overstyring.overstyr(ansattId, OverstyringData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))

                val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                repository.delete(entity)

                repository.findById(entity.id).isPresent shouldBe false
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
    }
}