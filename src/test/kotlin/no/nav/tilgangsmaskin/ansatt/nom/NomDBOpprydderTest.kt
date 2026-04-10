package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.assertSoftly
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.util.ReflectionTestUtils.setField
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.Instant.now
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.util.concurrent.atomic.AtomicInteger

@DataJpaTest
@Testcontainers
@AutoConfigureMetrics
@ContextConfiguration(classes = [TestApp::class, NomTjeneste::class, NomJPAAdapter::class, NomDBOpprydder::class, NomRaderFjernetTeller::class, NomKallTeller::class])
@ApplyExtension(SpringExtension::class)
class NomDBOpprydderTest : BehaviorSpec() {

    @MockkBean(relaxed = true)
    @Suppress("unused")
    private lateinit var token: Token

    @Autowired
    private lateinit var opprydder: NomDBOpprydder

    @Autowired
    private lateinit var repo: NomRepository

    @Autowired
    private lateinit var publisher: ApplicationEventPublisher

    @Autowired
    private lateinit var txManager: PlatformTransactionManager

    @Autowired
    private lateinit var antallKall: NomKallTeller

    @Autowired
    private lateinit var raderFjernet: NomRaderFjernetTeller




    init {
        beforeEach {
            TransactionTemplate(txManager).execute { repo.deleteAll() }
            setField(opprydder, "erLeder", false)
        }

        Given("ryddOpp") {
            When("pod er leder") {
                beforeEach { bliLeder() }
                Then("sletter rader med utgått gyldighet") {
                    lagre(FNR,LocalDate.now().minusDays(1))
                    lagre("20478606614", LocalDate.now().minusDays(1))
                    repo.count() shouldBe 2
                    assertSoftly {
                        opprydder.ryddOpp() shouldBe 2
                        repo.count() shouldBe 0
                    }
                }

                Then("beholder rader som fremdeles er gyldige") {
                    lagre(FNR,LocalDate.now().plusMonths(6))
                    repo.count() shouldBe 1
                    assertSoftly {
                        opprydder.ryddOpp() shouldBe 0
                        repo.count() shouldBe 1
                    }
                }

                Then("sletter kun utgåtte og beholder gyldige") {
                    lagre(FNR,LocalDate.now().minusDays(1))
                    val gyldig = lagre( "20478606614", LocalDate.now().plusMonths(6))

                    assertSoftly {
                        opprydder.ryddOpp() shouldBe 1
                        repo.count() shouldBe 1
                        repo.findById(gyldig.id!!).isPresent shouldBe true
                    }
                }

                Then("returnerer 0 nar ingen rader finnes") {
                    opprydder.ryddOpp() shouldBe 0
                }
            }

            When("pod er ikke leder") {
                Then("returnerer 0 uten å slette noe") {
                    lagre(FNR,LocalDate.now().minusDays(1))

                    assertSoftly {
                        opprydder.ryddOpp() shouldBe 0
                        repo.count() shouldBe 1
                    }
                }
            }
        }
    }

    private fun lagre(fnr: String, gyldigTil: LocalDate): NomEntity {
        val now = now()
        val gyldigtilInstant = gyldigTil.atStartOfDay().toInstant(UTC)
        return TransactionTemplate(txManager).execute {
            repo.save(NomEntity(nyttNavId(), fnr, now, gyldigtilInstant).also {
                it.created = now
                it.updated = now
            })
        }
    }

    private fun bliLeder() = setField(opprydder, "erLeder", true)
    private companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
        private fun nyttNavId() = "Z%06d".format(counter.incrementAndGet())
        private const val FNR = "08526835670"
        private val counter = AtomicInteger(0)
    }
}
