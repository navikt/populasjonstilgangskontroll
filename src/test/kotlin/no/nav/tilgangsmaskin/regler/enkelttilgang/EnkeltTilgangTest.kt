package no.nav.tilgangsmaskin.regler.enkelttilgang

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyEnhet.Enhet
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.utils.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangClientValidator.OverstyringException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ContextConfiguration
import no.nav.tilgangsmaskin.SharedPostgresContainer.postgreSQLContainer
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@EnableJpaAuditing
@Testcontainers
@AutoConfigureMetrics
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, LocalAuditor::class,EnkeltTilgangJPAAdapter::class])
@ApplyExtension(SpringExtension::class)
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
internal class EnkeltTilgangTest : BehaviorSpec() {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val historiskBrukerId = BrukerId("11111111111")


    @MockkBean
    private lateinit var vergemål: VergemålTjeneste

    @MockkBean
    private lateinit var nom: NomTjeneste

    @MockkBean
    lateinit var validator: KonsumentValidator
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
    @Autowired
    lateinit var adapter: EnkeltTilgangJPAAdapter
    @Autowired
    lateinit var repository: EnkeltTilgangRepository
    @MockK
    lateinit var ansatte: AnsattTjeneste
    @MockK
    lateinit var brukere: BrukerTjeneste

    init {
        lateinit var enkeltTilgang: EnkeltTilgangTjeneste

        beforeSpec {
            MockKAnnotations.init(this@EnkeltTilgangTest)
        }

        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns vanligBrukerId
            every { vergemål.vergemål(any()) } returns emptySet()
            every { validator.valider() } returns Unit
            every { token.erObo } returns false
            every { token.erCC } returns true
            every { token.system } returns "test"
            every { token.ansattId } returns ansattId
            every { token.systemNavn } returns "test"
            every { token.clusterAndSystem } returns "cluster:test"
            every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "Testenhet")
            every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
            enkeltTilgang = EnkeltTilgangTjeneste(ansatte, brukere, adapter, motor, proxy, validator, OverstyringTeller(registry, token))
        }

        Given("overstyring av tilgangsresultat") {

            When("OverstyringException kastes fra validator") {
                Then("kastes exception videre") {
                    every { validator.valider() } throws OverstyringException("ukjent system", "ukjent-system")
                    shouldThrow<OverstyringException> {
                        enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(vanligBrukerId, "Dette er test", IMORGEN))
                    }
                }
            }
        }

        Given("OverstyringEntity felter") {
            When("overstyring registreres") {
                Then("settes alle felter korrekt") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))
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
        }

        Given("erOverstyrt") {
            When("gyldig overstyring eksisterer via historisk ident") {
                Then("returneres true") {
                    val brukerMedHistorikk = BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns brukerMedHistorikk
                    every { brukere.brukerMedNærmesteFamilie(historiskBrukerId.verdi) } returns BrukerBuilder(historiskBrukerId).build()
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(historiskBrukerId, "Dette er en test", IMORGEN))
                    enkeltTilgang.harEnkeltTilgang(ansattId, BrukerBuilder(vanligBrukerId).build().brukerId).shouldBeTrue()
                }
            }
            When("det finnes flere overstyringer") {
                Then("gjelder den nyeste") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Denne er gammel", IGÅR))
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Denne er ny", IMORGEN))
                    enkeltTilgang.harEnkeltTilgang(ansattId, bruker.brukerId).shouldBeTrue()
                }
            }
            When("nyeste overstyring er utgått, eldre er aktiv") {
                Then("returneres false — nyeste vinner") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Denne er aktiv men gammel", IMORGEN))
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Denne er ny men utgått", IGÅR))
                    enkeltTilgang.harEnkeltTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
            When("overstyring er utgått") {
                Then("returneres false") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Denne er utgått", IGÅR))
                    enkeltTilgang.harEnkeltTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
            When("ingen overstyring er registrert") {
                Then("returneres false") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.harEnkeltTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
        }

        Given("overstyringer (bulk)") {
            When("aktive overstyringer eksisterer for flere brukere") {
                Then("returneres alle aktive brukerIds") {
                    val bruker1 = BrukerBuilder(vanligBrukerId).build()
                    val bruker2 = BrukerBuilder(historiskBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker1
                    every { brukere.brukerMedNærmesteFamilie(historiskBrukerId.verdi) } returns bruker2
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker1.brukerId, "Aktiv overstyring 1", IMORGEN))
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker2.brukerId, "Aktiv overstyring 2", IMORGEN))
                    val resultat = enkeltTilgang.tilganger(ansattId, listOf(bruker1.brukerId, bruker2.brukerId))
                    resultat shouldBe listOf(bruker1.brukerId, bruker2.brukerId)
                }
            }
            When("én overstyring er aktiv og én er utgått") {
                Then("returneres kun den aktive") {
                    val bruker1 = BrukerBuilder(vanligBrukerId).build()
                    val bruker2 = BrukerBuilder(historiskBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker1
                    every { brukere.brukerMedNærmesteFamilie(historiskBrukerId.verdi) } returns bruker2
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker1.brukerId, "Aktiv overstyring", IMORGEN))
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker2.brukerId, "Utgått overstyring", IGÅR))
                    val resultat = enkeltTilgang.tilganger(ansattId, listOf(bruker1.brukerId, bruker2.brukerId))
                    resultat shouldBe listOf(bruker1.brukerId)
                }
            }
            When("nyeste overstyring for en bruker er utgått, eldre er aktiv") {
                Then("returneres tom liste for den brukeren — nyeste vinner") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Aktiv men gammel", IMORGEN))
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Ny men utgått", IGÅR))
                    val resultat = enkeltTilgang.tilganger(ansattId, listOf(bruker.brukerId))
                    resultat.shouldBeEmpty()
                }
            }
            When("alle overstyringer er utgått") {
                Then("returneres tom liste") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Utgått overstyring", IGÅR))
                    val resultat = enkeltTilgang.tilganger(ansattId, listOf(bruker.brukerId))
                    resultat.shouldBeEmpty()
                }
            }
            When("ingen overstyringer er registrert") {
                Then("returneres tom liste") {
                    val resultat = enkeltTilgang.tilganger(ansattId, listOf(vanligBrukerId))
                    resultat.shouldBeEmpty()
                }
            }
        }

        Given("OverstyringEntityListener") {
            When("@PrePersist kalles") {
                Then("settes created, updated, oppretter og system") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))
                    val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    assertSoftly(entity) {
                        created shouldNotBe null
                        updated shouldNotBe null
                        created shouldBe updated
                        oppretter shouldBe ansattId.verdi
                        system shouldBe "test"
                    }
                }
            }
            When("@PostLoad kalles") {
                Then("lastes entity med korrekte felter fra database") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))
                    val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    val lastet = repository.findById(entity.id)
                    lastet.isPresent.shouldBeTrue()
                    with(lastet.get()) {
                        navid shouldBe ansattId.verdi
                        fnr shouldBe vanligBrukerId.verdi
                        created shouldNotBe null
                        updated shouldNotBe null
                        oppretter shouldBe ansattId.verdi
                        system shouldBe "test"
                    }
                }
            }
            When("@PreUpdate kalles") {
                Then("nullstilles system og oppretter til tokenverdi") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))
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
            }
            When("@PreRemove og @PostRemove kalles") {
                Then("fjernes entity fra database") {
                    val bruker = BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.overstyr(ansattId, EnkeltTilgangData(bruker.brukerId, "Dette er en begrunnelse", IMORGEN))
                    val entity = adapter.gjeldendeOverstyring(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    repository.delete(entity)
                    repository.findById(entity.id).isPresent shouldBe false
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = postgreSQLContainer
    }
}