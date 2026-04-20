package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.IGÅR
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.LocalDate.EPOCH

@DataJpaTest
@ContextConfiguration(classes = [NomJPAAdapter::class, TestApp::class])
@Testcontainers
@ApplyExtension(SpringExtension::class)
internal class NomTest : BehaviorSpec() {

    @Autowired
    private lateinit var nom: NomJPAAdapter

    @MockkBean
    private lateinit var token: Token

    init {
        beforeSpec {
            every { token.system } returns "test"
        }

        val ansattId = AnsattId("Z999999")
        val brukerId = BrukerId("08526835670")
        val utgått = NomAnsattData(ansattId, brukerId, NomAnsattPeriode(EPOCH, IGÅR))
        val gyldig = NomAnsattData(ansattId, brukerId)

        Given("fnrForAnsatt kalles") {
            When("ansatt er utgått") {
                Then("returneres ikke") {
                    nom.upsert(utgått)
                    nom.fnrForAnsatt(ansattId.verdi) shouldBe null
                }
            }

            When("ansatt har ingen sluttdato") {
                Then("er gyldig og returneres") {
                    nom.upsert(gyldig)
                    nom.fnrForAnsatt(ansattId.verdi) shouldNotBe null
                }
            }

            When("siste hendelse overstyrer forrige") {
                Then("siste hendelse gjelder") {
                    nom.upsert(utgått)
                    nom.fnrForAnsatt(ansattId.verdi) shouldBe null
                    nom.upsert(gyldig)
                    nom.fnrForAnsatt(ansattId.verdi) shouldBe gyldig.brukerId
                }
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}