package no.nav.tilgangsmaskin.ansatt.nom

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.SharedPostgresContainer.postgreSQLContainer
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.util.ReflectionTestUtils.setField
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant.now
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.util.concurrent.atomic.*

@DataJpaTest
@EnableJpaAuditing
@Testcontainers
@AutoConfigureMetrics
@ContextConfiguration(classes = [NomTjeneste::class, NomJPAAdapter::class, NomDBOpprydder::class])
@EnableAutoConfiguration
class NomDBOpprydderTest(private val opprydder: NomDBOpprydder, private val repo: NomRepository) : BehaviorSpec() {

    init {
        beforeEach {
            repo.deleteAll()
            setField(opprydder, "erLeder", false)
        }

        Given("ryddOpp") {
            When("pod er leder") {
                beforeEach { bliLeder() }
                Then("sletter rader med utgått gyldighet") {
                    lagre(FNR,LocalDate.now().minusDays(1))
                    lagre("20478606614", LocalDate.now().minusDays(1))
                    assertSoftly {
                        opprydder.ryddOpp() shouldBe 2
                        repo.count() shouldBe 0
                    }
                }

                Then("beholder rader som fremdeles er gyldige") {
                    lagre(FNR,LocalDate.now().plusMonths(6))
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
                        repo.findById(gyldig.id!!).isPresent.shouldBeTrue()
                    }
                }

                Then("returnerer 0 når ingen rader finnes") {
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
        val gyldigTilInstant = gyldigTil.atStartOfDay().toInstant(UTC)
        return repo.save(NomEntity(nyttNavId(), fnr, now(), gyldigTilInstant))
    }

    private fun bliLeder() = setField(opprydder, "erLeder", true)
    private companion object {
        @ServiceConnection
        private val postgres = postgreSQLContainer
        private fun nyttNavId() = "Z%06d".format(counter.incrementAndGet())
        private const val FNR = "08526835670"
        private val counter = AtomicInteger(0)
    }
}
