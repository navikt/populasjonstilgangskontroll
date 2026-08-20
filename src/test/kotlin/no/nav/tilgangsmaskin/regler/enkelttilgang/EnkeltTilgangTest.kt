package no.nav.tilgangsmaskin.regler.enkelttilgang

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micrometer.core.instrument.MeterRegistry
import io.opentelemetry.api.trace.Span
import io.mockk.every
import io.mockk.mockk
import no.nav.tilgangsmaskin.SharedPostgresContainer.postgreSQLContainer
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
import no.nav.tilgangsmaskin.felles.LocalAuditor
import no.nav.tilgangsmaskin.felles.TimeBeanConfig
import no.nav.tilgangsmaskin.felles.rest.PropertySettingTestContextInitializer
import no.nav.tilgangsmaskin.felles.rest.Token
import no.nav.tilgangsmaskin.felles.rest.TokenType.CCF
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@DataJpaTest
@EnableJpaAuditing
@Testcontainers
@AutoConfigureMetrics
@ContextConfiguration(initializers = [PropertySettingTestContextInitializer::class],classes = [LocalAuditor::class,EnkeltTilgangJPAAdapter::class])
@Import(TimeBeanConfig::class)
@EnableAutoConfiguration
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
internal class EnkeltTilgangTest(
    private val motor: RegelMotor,
    private val registry: MeterRegistry,
    private val adapter: EnkeltTilgangJPAAdapter,
    private val repo: EnkeltTilgangRepository,
) : BehaviorSpec() {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val historiskBrukerId = BrukerId("11111111111")

    @MockkBean
    private lateinit var vergemål: VergemålTjeneste
    @MockkBean
    private lateinit var nom: NomTjeneste
    @MockkBean
    lateinit var proxy: EntraProxyTjeneste
    @MockkBean
    lateinit var token: Token
    @MockkBean
    lateinit var oppfølging: OppfølgingTjeneste
    private val ansatte: AnsattTjeneste = mockk()
    private val brukere: BrukerTjeneste = mockk()
    private lateinit var enkeltTilgang: EnkeltTilgangTjeneste

    init {
        beforeEach {
            stubStandardMocks()
            enkeltTilgang = EnkeltTilgangTjeneste(
                ansatte,
                brukere,
                adapter,
                motor,
                proxy,
                EnkeltTilgangTeller(registry, token),
            )
        }


        Given("OverstyringEntity felter") {
            When("enkelttilgang registreres") {
                Then("settes alle felter korrekt") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Dette er en begrunnelse")
                    val entity = adapter.gjeldendeTilgang(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
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
                        span shouldBe Span.current().spanContext.spanId
                    }
                }
            }
        }

        Given("har enkelttilgang") {
            When("gyldig enkelttilgang eksisterer via historisk ident") {
                Then("returneres true") {
                    val brukerMedHistorikk = BrukerBuilder(vanligBrukerId).historiske(setOf(historiskBrukerId)).build()
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns brukerMedHistorikk
                    every { brukere.medNærmesteFamilie(historiskBrukerId.verdi) } returns vanligBruker(historiskBrukerId)
                    registrerEnkelttilgang(historiskBrukerId)
                    enkeltTilgang.harTilgang(ansattId, vanligBruker(vanligBrukerId).brukerId).shouldBeTrue()
                }
            }
            When("det finnes flere enkelttilganger") {
                Then("gjelder den nyeste") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Denne er gammel", IGÅR)
                    registrerEnkelttilgang(bruker.brukerId, "Denne er ny", IMORGEN)
                    enkeltTilgang.harTilgang(ansattId, bruker.brukerId).shouldBeTrue()
                }
            }
            When("nyeste enkelttilgang er utgått, eldre er aktiv") {
                Then("returneres false — nyeste vinner") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Denne er aktiv men gammel", IMORGEN)
                    registrerEnkelttilgang(bruker.brukerId, "Denne er ny men utgått", IGÅR)
                    enkeltTilgang.harTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
            When("enkelttilgang er utgått") {
                Then("returneres false") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Denne er utgått", IGÅR)
                    enkeltTilgang.harTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
            When("ingen enkelttilgang er registrert") {
                Then("returneres false") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    enkeltTilgang.harTilgang(ansattId, bruker.brukerId) shouldBe false
                }
            }
        }

        Given("enkelttilganger (bulk)") {
            When("aktive enkelttilganger eksisterer for flere brukere") {
                Then("returneres alle aktive brukerIds") {
                    val bruker1 = vanligBruker(vanligBrukerId)
                    val bruker2 = vanligBruker(historiskBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker1
                    every { brukere.medNærmesteFamilie(historiskBrukerId.verdi) } returns bruker2
                    registrerEnkelttilgang(bruker1.brukerId, "Aktiv enkelttilgang 1")
                    registrerEnkelttilgang(bruker2.brukerId, "Aktiv enkelttilgang 2")
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(bruker1.brukerId, bruker2.brukerId))
                    resultat shouldBe listOf(bruker1.brukerId, bruker2.brukerId)
                }
            }
            When("én enkelttilgang er aktiv og én er utgått") {
                Then("returneres kun den aktive") {
                    val bruker1 = vanligBruker(vanligBrukerId)
                    val bruker2 = vanligBruker(historiskBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker1
                    every { brukere.medNærmesteFamilie(historiskBrukerId.verdi) } returns bruker2
                    registrerEnkelttilgang(bruker1.brukerId, "Aktiv enkelttilgang")
                    registrerEnkelttilgang(bruker2.brukerId, "Utgått enkelttilgang", IGÅR)
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(bruker1.brukerId, bruker2.brukerId))
                    resultat shouldBe listOf(bruker1.brukerId)
                }
            }
            When("nyeste enkelttilgang for en bruker er utgått, eldre er aktiv") {
                Then("returneres tom liste for den brukeren — nyeste vinner") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Aktiv men gammel", IMORGEN)
                    registrerEnkelttilgang(bruker.brukerId, "Ny men utgått", IGÅR)
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(bruker.brukerId))
                    resultat.shouldBeEmpty()
                }
            }
            When("alle enkelttilganger er utgått") {
                Then("returneres tom liste") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Utgått enkelttilgang", IGÅR)
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(bruker.brukerId))
                    resultat.shouldBeEmpty()
                }
            }
            When("ingen enkelttilganger er registrert") {
                Then("returneres tom liste") {
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(vanligBrukerId))
                    resultat.shouldBeEmpty()
                }
            }
        }

        Given("enkeltTilgangEntityListener") {
            When("entity persisteres") {
                Then("settes created, updated, oppretter, system og span") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Dette er en begrunnelse")
                    val entity = adapter.gjeldendeTilgang(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    assertSoftly(entity) {
                        created shouldNotBe null
                        updated shouldNotBe null
                        created shouldBe updated
                        oppretter shouldBe ansattId.verdi
                        system shouldBe "test"
                        span shouldBe Span.current().spanContext.spanId
                    }
                }
            }
            When("entity lastes fra database") {
                Then("lastes entity med korrekte felter") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Dette er en begrunnelse")
                    val entity = adapter.gjeldendeTilgang(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    val lastet = repo.findById(entity.id!!)
                    lastet.isPresent.shouldBeTrue()
                    with(lastet.get()) {
                        navid shouldBe ansattId.verdi
                        fnr shouldBe vanligBrukerId.verdi
                        created shouldNotBe null
                        updated shouldNotBe null
                        oppretter shouldBe ansattId.verdi
                        system shouldBe "test"
                        span shouldBe Span.current().spanContext.spanId
                    }
                }
            }
            When("entity oppdateres") {
                Then("resettes system, oppretter og span til gjeldende verdier") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Dette er en begrunnelse")
                    val entity = adapter.gjeldendeTilgang(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    val createdFør = entity.created
                    entity.system = "ukjent-system"
                    entity.oppretter = "X000000"
                    entity.span = "1234567890abcdef"
                    repo.saveAndFlush(entity)
                    val oppdatert = repo.findById(entity.id!!).get()
                    assertSoftly(oppdatert) {
                        system shouldBe "test"
                        oppretter shouldBe ansattId.verdi
                        span shouldBe Span.current().spanContext.spanId
                        created shouldBe createdFør
                    }
                }
            }
            When("entity slettes") {
                Then("fjernes entity fra database") {
                    val bruker = vanligBruker(vanligBrukerId)
                    every { brukere.medNærmesteFamilie(vanligBrukerId.verdi) } returns bruker
                    registrerEnkelttilgang(bruker.brukerId, "Dette er en begrunnelse")
                    val entity = adapter.gjeldendeTilgang(ansattId.verdi, vanligBrukerId.verdi, emptyList())!!
                    repo.delete(entity)
                    repo.findById(entity.id!!).isPresent shouldBe false
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = postgreSQLContainer
    }

    private fun stubStandardMocks() {
        every { nom.fnrForAnsatt(any()) } returns vanligBrukerId
        every { vergemål.alle(any()) } returns emptySet()
        every { token.type } returns CCF
        every { token.system } returns "test"
        every { token.ansattId } returns ansattId
        every { token.systemNavn } returns "test"
        every { token.clusterAndSystem } returns "cluster:test"
        every { proxy.enhet(ansattId) } returns Enhet(Enhetsnummer("1234"), "Testenhet")
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
    }

    private fun vanligBruker(brukerId: BrukerId) = BrukerBuilder(brukerId).build()

    private fun registrerEnkelttilgang(
        brukerId: BrukerId,
        begrunnelse: String = "Dette er en test",
        gyldigTil: LocalDate = IMORGEN,
    ) = enkeltTilgang.registrerTilgang(ansattId, EnkeltTilgangData(brukerId, begrunnelse, gyldigTil))
}