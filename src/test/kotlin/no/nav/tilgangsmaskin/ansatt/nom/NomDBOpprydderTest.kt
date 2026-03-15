package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.verify
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.net.InetAddress
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.atomic.*

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@ContextConfiguration(classes = [TestApp::class, NomTjeneste::class, NomJPAAdapter::class, NomDBOpprydder::class])
@ApplyExtension(SpringExtension::class)
class NomDBOpprydderTest : DescribeSpec() {

    @MockkBean(relaxed = true) lateinit var token: Token
    @MockkBean(relaxed = true) lateinit var antallKall: NomKallTeller
    @MockkBean(relaxed = true) lateinit var raderFjernet: NomRaderFjernetTeller

    @Autowired lateinit var opprydder: NomDBOpprydder
    @Autowired lateinit var repo: NomRepository
    @Autowired lateinit var publisher: ApplicationEventPublisher
    @Autowired lateinit var txManager: PlatformTransactionManager

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")

        private val hostname = InetAddress.getLocalHost().hostName
        private val counter = AtomicInteger(0)
    }

    private fun nyttNavId() = "Z%06d".format(counter.incrementAndGet())

    init {
        beforeEach {
            TransactionTemplate(txManager).execute { repo.deleteAll() }
            clearMocks(antallKall, raderFjernet)
            ReflectionTestUtils.setField(opprydder, "erLeder", false)
        }

        describe("ryddOpp") {

            describe("som leder") {

                it("sletter rader med utgått gyldigtil") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))
                    lagre(fnr = "20478606614", gyldigTil = LocalDate.now().minusDays(1))

                    blirLeder()
                    val antall = opprydder.ryddOpp()

                    antall shouldBe 2
                    repo.count() shouldBe 0
                }

                it("beholder rader der gyldigtil er i fremtiden") {
                    lagre(gyldigTil = LocalDate.now().plusMonths(6))

                    blirLeder()
                    val antall = opprydder.ryddOpp()

                    antall shouldBe 0
                    repo.count() shouldBe 1
                }

                it("sletter kun utgåtte og beholder gyldige") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))
                    val gyldig = lagre(fnr = "20478606614", gyldigTil = LocalDate.now().plusMonths(6))

                    blirLeder()
                    val antall = opprydder.ryddOpp()

                    antall shouldBe 1
                    repo.count() shouldBe 1
                    repo.findById(gyldig.id!!).isPresent shouldBe true
                }

                it("returnerer 0 når ingen rader finnes") {
                    blirLeder()
                    opprydder.ryddOpp() shouldBe 0
                }

                it("oppdaterer antallKall-teller") {
                    blirLeder()
                    opprydder.ryddOpp()

                    verify(atLeast = 1) { antallKall.tell() }
                }

                it("oppdaterer raderFjernet-teller med antall slettede rader") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))

                    blirLeder()
                    opprydder.ryddOpp()

                    verify { raderFjernet.tell(1) }
                }
            }

            describe("ikke som leder") {

                it("returnerer 0 uten å slette noe") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))

                    opprydder.ryddOpp() shouldBe 0

                    repo.count() shouldBe 1
                }

                it("kaller ikke tellere") {
                    opprydder.ryddOpp()

                    verify(exactly = 0) { antallKall.tell() }
                    verify(exactly = 0) { raderFjernet.tell(any()) }
                }
            }

            describe("doHandleLeaderChange") {

                it("kjører ryddOpp når denne instansen blir leder") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))

                    publisher.publishEvent(LeaderChangedEvent(this, hostname))

                    verify { antallKall.tell() }
                    verify { raderFjernet.tell(1) }
                }

                it("kjører ikke ryddOpp når en annen instans blir leder") {
                    lagre(gyldigTil = LocalDate.now().minusDays(1))

                    publisher.publishEvent(LeaderChangedEvent(this, "en-annen-host"))

                    verify(exactly = 0) { antallKall.tell() }
                }
            }
        }
    }

    private fun lagre(fnr: String = "08526835670", gyldigTil: LocalDate): NomEntity {
        val now = Instant.now()
        val gyldigtilInstant = gyldigTil.atStartOfDay().toInstant(ZoneOffset.UTC)
        return TransactionTemplate(txManager).execute {
            repo.save(NomEntity(nyttNavId(), fnr, now, gyldigtilInstant).also {
                it.created = now
                it.updated = now
            })
        }!!
    }

    // Sets erLeder=true directly to avoid triggering doHandleLeaderChange as a side effect.
    // doHandleLeaderChange is tested separately in the "doHandleLeaderChange" describe block.
    private fun blirLeder() = ReflectionTestUtils.setField(opprydder, "erLeder", true)
}
