package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyAnsatt.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.RegelTestConfig
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
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
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class,LocalAuditor::class])
@AutoConfigureMetrics
@Testcontainers
@ActiveProfiles(ClusterConstants.TEST)
@ApplyExtension(SpringExtension::class)
class OverstyringRegelTjenesteTest : DescribeSpec() {

    private val strengtFortroligAktørId = `AktørId`("1234567890123")
    private val strengtFortroligBrukerId = BrukerId("08526835671")
    private val fortroligBrukerId = BrukerId("08526835672")
    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val dnr = BrukerId("12345678910")

    @Autowired
    lateinit var registry: MeterRegistry
    @Autowired
    lateinit var repo: OverstyringRepository
    @MockkBean
    lateinit var token: Token
    @MockkBean
    lateinit var oppfølging: `OppfølgingTjeneste`
    @MockkBean
    lateinit var validator: OverstyringClientValidator
    @MockkBean
    lateinit var proxy: EntraProxyTjeneste
    @Autowired
    lateinit var motor: RegelMotor
    @MockK
    lateinit var brukere: BrukerTjeneste
    @MockK
    lateinit var ansatte: AnsattTjeneste

    init {
        lateinit var overstyring: OverstyringTjeneste
        lateinit var regler: RegelTjeneste

        beforeSpec {
            MockKAnnotations.init(this@OverstyringRegelTjenesteTest)
        }

        beforeEach {
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
            overstyring = OverstyringTjeneste(ansatte, brukere, OverstyringJPAAdapter(repo), motor, proxy, validator,
                OverstyringTeller(registry, token))
            regler = RegelTjeneste(motor, brukere, ansatte, overstyring, LocalAuditor())
        }

        describe("bulk") {

            it("brukere uten tilgang havner i avviste") {
                every {
                    brukere.brukere(setOf(strengtFortroligAktørId.verdi, fortroligBrukerId.verdi))
                } returns setOf(
                    BrukerBuilder(strengtFortroligBrukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).oppslagId(strengtFortroligAktørId.verdi).aktørId(strengtFortroligAktørId).build(),
                    BrukerBuilder(fortroligBrukerId).kreverMedlemskapI(GlobalGruppe.FORTROLIG).build())

                val resultater = regler.bulkRegler(ansattId,
                    setOf(BrukerIdOgRegelsett(strengtFortroligAktørId.verdi),
                        BrukerIdOgRegelsett(fortroligBrukerId.verdi)))

                resultater.avviste shouldHaveSize 2
                resultater.godkjente.shouldBeEmpty()
                resultater.ukjente.shouldBeEmpty()
            }

            it("avvist bruker godkjennes når overstyring er registrert") {
                every {
                    brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
                } returns BrukerBuilder(vanligBrukerId, GeografiskTilknytning.UtenlandskTilknytning()).kreverMedlemskapI(
                    GlobalGruppe.UTENLANDSK).build()
                every {
                    brukere.brukere(setOf(vanligBrukerId.verdi))
                } returns setOf(BrukerBuilder(vanligBrukerId, GeografiskTilknytning.UtenlandskTilknytning()).kreverMedlemskapI(
                    GlobalGruppe.UTENLANDSK).build())
                overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er en test",
                    IMORGEN))

                val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))

                resultater.avviste.shouldBeEmpty()
                resultater.godkjente shouldHaveSize 1
            }

            it("dnr som er erstattet med fnr avvises ikke") {
                every {
                    brukere.brukere(setOf(dnr.verdi))
                } returns setOf(BrukerBuilder(vanligBrukerId).oppslagId(dnr.verdi).historiske(setOf(dnr)).build())

                val resultat = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(dnr.verdi)))
                resultat.godkjente shouldHaveSize 1
            }
        }

        describe("kompletteRegler") {

            it("dnr som er erstattet med fnr avvises ikke") {
                every {
                    brukere.brukerMedNærmesteFamilie(dnr.verdi)
                } returns BrukerBuilder(vanligBrukerId).historiske(setOf(dnr)).build()

                shouldNotThrowAny { regler.kompletteRegler(ansattId, dnr.verdi) }
            }

            it("tilgang gis når overstyring er registrert og en regel avslår") {
                every {
                    brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
                } returns BrukerBuilder(vanligBrukerId).build()
                overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))

                shouldNotThrowAny { regler.kompletteRegler(ansattId, vanligBrukerId.verdi) }
            }

            it("tilgang gis ikke når regel avslår og ingen overstyring er registrert") {
                every {
                    brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
                } returns BrukerBuilder(vanligBrukerId, GeografiskTilknytning.UtenlandskTilknytning()).kreverMedlemskapI(
                    GlobalGruppe.UTENLANDSK).build()

                shouldThrow<RegelException> { regler.kompletteRegler(ansattId, vanligBrukerId.verdi) }
            }
        }

        describe("kjerneregler") {

            it("kjøres uten unntak når bruker finnes i PDL") {
                every {
                    brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
                } returns BrukerBuilder(vanligBrukerId).build()

                shouldNotThrowAny { regler.kjerneregler(ansattId, vanligBrukerId.verdi) }
            }
        }

        describe("overstyring") {

            it("registreres ikke når kjerneregler avslår") {
                every {
                    brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
                } returns BrukerBuilder(vanligBrukerId).kreverMedlemskapI(GlobalGruppe.STRENGT_FORTROLIG).build()

                shouldThrow<RegelException> {
                    overstyring.overstyr(ansattId,
                        OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
                }
                overstyring.erOverstyrt(ansattId, vanligBrukerId) shouldBe false
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
    }
}