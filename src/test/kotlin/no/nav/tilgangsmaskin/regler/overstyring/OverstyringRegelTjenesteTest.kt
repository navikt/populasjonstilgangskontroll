package no.nav.tilgangsmaskin.regler.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.RegelMotorTestConfig
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ContextConfiguration
import no.nav.tilgangsmaskin.SharedPostgresContainer
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@EnableJpaAuditing
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, RegelMotorTestConfig::class, OverstyringTjeneste::class,OverstyringJPAAdapter::class,RegelTjeneste::class,LocalAuditor::class])
@AutoConfigureMetrics
@Testcontainers
@ApplyExtension(SpringExtension::class)
class OverstyringRegelTjenesteTest : BehaviorSpec() {

    private val strengtFortroligAktørId = AktørId("1234567890123")
    private val strengtFortroligBrukerId = BrukerId("08526835671")
    private val fortroligBrukerId = BrukerId("08526835672")
    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val dnr = BrukerId("12345678910")

    @MockkBean
    private lateinit var vergemål: VergemålTjeneste

    @MockkBean
    private lateinit var nom: NomTjeneste

    @MockkBean
    lateinit var token: Token
    @MockkBean
    lateinit var oppfølging: OppfølgingTjeneste
    @MockkBean
    lateinit var validator: OverstyringClientValidator
    @MockkBean
    lateinit var proxy: EntraProxyTjeneste

    @MockkBean
    lateinit var brukere: BrukerTjeneste
    @MockkBean
    lateinit var ansatte: AnsattTjeneste

    @Autowired
    lateinit var motor: RegelMotor
    @Autowired
    lateinit var overstyring: OverstyringTjeneste
    @Autowired
    lateinit var regler: RegelTjeneste



    init {

        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns vanligBrukerId
            every { vergemål.vergemål(any()) } returns emptySet()
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
        }

        Given("bulk") {
            When("brukere krever spesialtilganger ansatt mangler") {
                Then("havner de i avviste") {
                    every {
                        brukere.brukere(setOf(strengtFortroligAktørId.verdi, fortroligBrukerId.verdi))
                    } returns setOf(
                        BrukerBuilder(strengtFortroligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).oppslagId(strengtFortroligAktørId.verdi).aktørId(strengtFortroligAktørId).build(),
                        BrukerBuilder(fortroligBrukerId).kreverMedlemskapI(FORTROLIG).build())

                    val resultater = regler.bulkRegler(ansattId,
                        setOf(BrukerIdOgRegelsett(strengtFortroligAktørId.verdi), BrukerIdOgRegelsett(fortroligBrukerId.verdi)))

                    assertSoftly(resultater) {
                        avviste shouldHaveSize 2
                        godkjente.shouldBeEmpty()
                        ukjente.shouldBeEmpty()
                    }
                }
            }
            When("avvist bruker og overstyring er registrert") {
                Then("godkjennes bruker") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build())
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er en test", IMORGEN))

                    val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))
                    assertSoftly(resultater) {
                        avviste.shouldBeEmpty()
                        godkjente shouldHaveSize 1
                    }
                }
            }
            When("dnr er erstattet med fnr") {
                Then("avvises ikke") {
                    every { brukere.brukere(setOf(dnr.verdi)) } returns setOf(BrukerBuilder(vanligBrukerId).oppslagId(dnr.verdi).historiske(setOf(dnr)).build())
                    regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(dnr.verdi))).godkjente shouldHaveSize 1
                }
            }
        }

        Given("kompletteRegler") {
            When("dnr er erstattet med fnr") {
                Then("avvises ikke") {
                    every { brukere.brukerMedNærmesteFamilie(dnr.verdi) } returns BrukerBuilder(vanligBrukerId).historiske(setOf(dnr)).build()
                    shouldNotThrowAny { regler.kompletteRegler(ansattId, dnr.verdi) }
                }
            }
            When("overstyring er registrert og en regel avslår") {
                Then("gis tilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN))
                    shouldNotThrowAny { regler.kompletteRegler(ansattId, vanligBrukerId.verdi) }
                }
            }
            When("regel avslår og ingen overstyring er registrert") {
                Then("gis ikke tilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    shouldThrow<RegelException> { regler.kompletteRegler(ansattId, vanligBrukerId.verdi) }
                }
            }
        }

        Given("kjerneregler") {
            When("bruker finnes i PDL") {
                Then("kjøres uten unntak") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    shouldNotThrowAny { regler.kjerneregler(ansattId, vanligBrukerId.verdi) }
                }
            }
        }

        Given("overstyring") {
            When("kjerneregler avslår") {
                Then("registreres ikke overstyring") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                    shouldThrow<RegelException> { overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Dette er test", IMORGEN)) }
                    overstyring.erOverstyrt(ansattId, vanligBrukerId) shouldBe false
                }
            }
            When("overstyring har utløpt") {
                Then("erOverstyrt returnerer false") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Utløpt test", IGÅR))
                    overstyring.erOverstyrt(ansattId, vanligBrukerId) shouldBe false
                }
            }
            When("overstyring er gyldig") {
                Then("erOverstyrt returnerer true") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Gyldig test", IMORGEN))
                    overstyring.erOverstyrt(ansattId, vanligBrukerId) shouldBe true
                }
            }
            When("bulk overstyringer med utløpt og gyldig") {
                Then("kun gyldige returneres") {
                    val annenBrukerId = BrukerId("08526835673")
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(annenBrukerId.verdi) } returns BrukerBuilder(annenBrukerId).build()
                    overstyring.overstyr(ansattId, OverstyringData(vanligBrukerId, "Gyldig", IMORGEN))
                    overstyring.overstyr(ansattId, OverstyringData(annenBrukerId, "Utløpt", IGÅR))
                    val resultat = overstyring.overstyringer(ansattId, listOf(vanligBrukerId, annenBrukerId))
                    resultat shouldHaveSize 1
                    resultat.single() shouldBe vanligBrukerId
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = SharedPostgresContainer.instance
    }
}