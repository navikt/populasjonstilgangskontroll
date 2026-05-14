package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@DataJpaTest
@Testcontainers
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class, NomTjeneste::class, NomJPAAdapter::class])
@ApplyExtension(SpringExtension::class)
class NomTjenesteTest : BehaviorSpec() {

    @Autowired private lateinit var tjeneste: NomTjeneste
    @Autowired private lateinit var adapter: NomJPAAdapter

    @MockkBean private lateinit var token: Token

    private val ansattId = AnsattId("Z999999")
    private val brukerId = BrukerId("08526835670")

    init {
        Given("fnrForAnsatt kalles") {
            When("ansatt finnes") {
                Then("returnerer fnr") {
                    adapter.upsert(NomAnsattData(ansattId, brukerId))
                    tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId
                }
            }

            When("ansatt ikke finnes") {
                Then("returnerer null") {
                    tjeneste.fnrForAnsatt(AnsattId("X000000")) shouldBe null
                }
            }
        }

        Given("lagre kalles") {
            When("ny ansatt lagres") {
                Then("kan hentes igjen") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId))
                    tjeneste.fnrForAnsatt(ansattId) shouldNotBe null
                }
            }

            When("eksisterende ansatt oppdateres") {
                Then("nyeste fnr returneres") {
                    val nyttBrukerId = BrukerId("12345678901")
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId))
                    tjeneste.lagre(NomAnsattData(ansattId, nyttBrukerId))
                    tjeneste.fnrForAnsatt(ansattId) shouldBe nyttBrukerId
                }
            }
        }

        Given("ryddOpp kalles") {
            When("ingen utgåtte ansatte") {
                Then("returnerer 0") {
                    tjeneste.ryddOpp() shouldBe 0
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
    }
}
