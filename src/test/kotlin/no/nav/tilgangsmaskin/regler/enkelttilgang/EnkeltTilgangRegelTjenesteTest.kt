package no.nav.tilgangsmaskin.regler.enkelttilgang

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UTENLANDSK
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
import no.nav.tilgangsmaskin.felles.LocalAuditor
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IMORGEN
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.RegelTjeneste
import no.nav.tilgangsmaskin.regler.BulkResponsAggregator
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
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, EnkeltTilgangTjeneste::class, EnkeltTilgangJPAAdapter::class, RegelTjeneste::class, BulkResponsAggregator::class, LocalAuditor::class])
@AutoConfigureMetrics
@Testcontainers
@ComponentScan("no.nav.tilgangsmaskin.regler.motor")
@ApplyExtension(SpringExtension::class)
class EnkeltTilgangRegelTjenesteTest : BehaviorSpec() {

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
    lateinit var proxy: EntraProxyTjeneste

    @MockkBean
    lateinit var brukere: BrukerTjeneste
    @MockkBean
    lateinit var ansatte: AnsattTjeneste

    @Autowired
    lateinit var motor: RegelMotor
    @Autowired
    lateinit var enkeltTilgang: EnkeltTilgangTjeneste
    @Autowired
    lateinit var regler: RegelTjeneste


    init {

        beforeEach {
            every { nom.fnrForAnsatt(any()) } returns vanligBrukerId
            every { vergemål.vergemål(any()) } returns emptySet()
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

        Given("bulk-oppslag med enkelttilgang") {
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
            When("avvist bruker og enkelttilgang er registrert") {
                Then("godkjennes bruker") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi)) } returns setOf(BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build())
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Dette er en test", IMORGEN))

                    val resultater = regler.bulkRegler(ansattId, setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi)))
                    assertSoftly(resultater) {
                        avviste.shouldBeEmpty()
                        godkjente shouldHaveSize 1
                    }
                }
            }
            When("én bruker har enkelttilgang og én mangler det i bulk") {
                Then("godkjennes kun bruker med enkelttilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    every { brukere.brukere(setOf(vanligBrukerId.verdi, fortroligBrukerId.verdi)) } returns setOf(
                        BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build(),
                        BrukerBuilder(fortroligBrukerId).kreverMedlemskapI(FORTROLIG).build()
                    )
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Har enkelttilgang", IMORGEN))

                    val resultater = regler.bulkRegler(ansattId,
                        setOf(BrukerIdOgRegelsett(vanligBrukerId.verdi), BrukerIdOgRegelsett(fortroligBrukerId.verdi)))

                    assertSoftly(resultater) {
                        godkjente shouldHaveSize 1
                        godkjente.first().brukerId shouldBe vanligBrukerId.verdi
                        avviste shouldHaveSize 1
                        avviste.first().brukerId shouldBe fortroligBrukerId.verdi
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
                    shouldNotThrowAny {
                        regler.kompletteRegler(ansattId, dnr.verdi)
                    }
                }
            }
            When("enkelttilgang er registrert og en regel avslår") {
                Then("gis tilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Dette er test", IMORGEN))
                    shouldNotThrowAny {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
            When("regel avslår og ingen enkelttilgang er registrert") {
                Then("gis ikke tilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId, UtenlandskTilknytning()).kreverMedlemskapI(UTENLANDSK).build()
                    shouldThrow<RegelException> {
                        regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
        }

        Given("kjerneregler") {
            When("bruker finnes i PDL") {
                Then("kjøres uten unntak") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    shouldNotThrowAny {
                        regler.kjerneregler(ansattId, vanligBrukerId.verdi)
                    }
                }
            }
        }

        Given("enkelttilgang") {
            When("kjerneregler avslår") {
                Then("registreres ikke enkelttilgang") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).kreverMedlemskapI(STRENGT_FORTROLIG).build()
                    shouldThrow<RegelException> {
                        enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Dette er test", IMORGEN))
                    }
                    enkeltTilgang.harEnkeltTilgang(ansattId, vanligBrukerId) shouldBe false
                }
            }
            When("enkelttilgang har utløpt") {
                Then("harEnkelttilgang returnerer false") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Utløpt test", IGÅR))
                    enkeltTilgang.harEnkeltTilgang(ansattId, vanligBrukerId) shouldBe false
                }
            }
            When("enkelttilgang er gyldig") {
                Then("harEnkelttilgang returnerer true") {
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Gyldig test", IMORGEN))
                    enkeltTilgang.harEnkeltTilgang(ansattId, vanligBrukerId).shouldBeTrue()
                }
            }
            When("bulk enkelttilganger  med utløpt og gyldig") {
                Then("kun gyldige returneres") {
                    val annenBrukerId = BrukerId("08526835673")
                    every { brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi) } returns BrukerBuilder(vanligBrukerId).build()
                    every { brukere.brukerMedNærmesteFamilie(annenBrukerId.verdi) } returns BrukerBuilder(annenBrukerId).build()
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(vanligBrukerId, "Gyldig", IMORGEN))
                    enkeltTilgang.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(annenBrukerId, "Utløpt", IGÅR))
                    val resultat = enkeltTilgang.tilganger(ansattId, setOf(vanligBrukerId, annenBrukerId))
                    resultat shouldHaveSize 1
                    resultat.single() shouldBe vanligBrukerId
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = postgreSQLContainer
    }
}