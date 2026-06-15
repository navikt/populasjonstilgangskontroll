package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkSpyBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.verify
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_CACHE
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjenesteTest.OppfølgingTestConfig
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.CacheTestConfig
import no.nav.tilgangsmaskin.felles.cache.getOne
import no.nav.tilgangsmaskin.felles.cache.getMany
import no.nav.tilgangsmaskin.tilgang.Token


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import no.nav.tilgangsmaskin.SharedPostgresContainer.postgreSQLContainer
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Startet
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant.parse
import java.util.UUID
import java.util.UUID.*

@DataJpaTest
@EnableJpaAuditing
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, OppfølgingTjeneste::class, OppfølgingJPAAdapter::class])
@Import(OppfølgingTestConfig::class)
@ApplyExtension(SpringExtension::class)
class OppfølgingTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    class OppfølgingTestConfig : CacheTestConfig(OPPFØLGING)

    @MockkBean private lateinit var token: Token

    @MockkBean(relaxed = true) private lateinit var teller: OppfølgingkontorTeller

    @MockkSpyBean
    private lateinit var adapter: OppfølgingJPAAdapter

    @Autowired
    private lateinit var tjeneste: OppfølgingTjeneste

    @Autowired
    private lateinit var cache: CacheOperations

    init {
        beforeEach { cache.clear(OPPFØLGING_CACHE) }

        fun startet(periode: UUID = randomUUID(), kontor: Kontor = KONTOR) =
            Startet(periode, IDENTER, kontor, parse("2024-01-01T09:00:00Z"))

        Given("enhetFor") {
            When("det ikke finnes oppfølging") {
                Then("returneres null og adapter kalles") {
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe null
                    verify(exactly = 1) { adapter.enhetFor(brukerId.verdi) }
                }
            }

            When("oppfølging er registrert via registrer()") {
                Then("returneres enhet fra cache uten adapter-kall") {
                    tjeneste.registrer(startet())
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe kontor
                    verify(exactly = 0) { adapter.enhetFor(brukerId.verdi) }
                }
            }

            When("oppfølging finnes i DB men ikke i cache") {
                Then("hentes fra adapter og lagres i cache") {
                    tjeneste.registrer(startet())
                    cache.clear(OPPFØLGING_CACHE)
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe kontor
                    verify(exactly = 1) { adapter.enhetFor(brukerId.verdi) }
                    cache.getOne<Enhetsnummer>(OPPFØLGING_CACHE, brukerId.verdi) shouldBe kontor
                }
            }

            When("oppslag via aktørId") {
                Then("returneres enhet fra cache populert ved registrering") {
                    tjeneste.registrer(startet())
                    tjeneste.enhetFor(Identifikator(aktørId.verdi)) shouldBe kontor
                    verify(exactly = 0) { adapter.enhetFor(aktørId.verdi) }
                }
            }
        }

        Given("registrer") {
            When("registrering utføres") {
                Then("populeres cache for brukerId og aktørId") {
                    tjeneste.registrer(startet())
                    cache.getMany<Enhetsnummer>(OPPFØLGING_CACHE, setOf(brukerId.verdi, aktørId.verdi)).size shouldBe 2
                }
            }

            When("re-registrering med ny enhet") {
                Then("oppdateres cache med ny verdi") {
                    val id = randomUUID()
                    tjeneste.registrer(startet(id))
                    cache.getOne<Enhetsnummer>(OPPFØLGING_CACHE, brukerId.verdi) shouldBe kontor

                    val nyEnhet = Enhetsnummer("5678")
                    tjeneste.registrer(Startet(id, IDENTER, Kontor(nyEnhet, "Ny enhet"), parse("2024-06-01T09:00:00Z")))
                    cache.getOne<Enhetsnummer>(OPPFØLGING_CACHE, brukerId.verdi) shouldBe nyEnhet
                    cache.getOne<Enhetsnummer>(OPPFØLGING_CACHE, aktørId.verdi) shouldBe nyEnhet
                }
            }
        }

        Given("avslutning av oppfølging") {
            When("avslutt kalles etter registrering") {
                Then("fjernes cache-innslag for brukerId og aktørId") {
                    val id = randomUUID()
                    tjeneste.registrer(startet(id))
                    cache.getMany<Enhetsnummer>(OPPFØLGING_CACHE, setOf(brukerId.verdi, aktørId.verdi)).size shouldBe 2
                    tjeneste.avslutt(Avsluttet(id, IDENTER))
                    cache.getMany<Enhetsnummer>(OPPFØLGING_CACHE, setOf(brukerId.verdi, aktørId.verdi)) shouldBe emptyMap()
                }
            }
        }
    }

    companion object {
        private val brukerId = BrukerId("08526835670")
        private val aktørId  = AktørId("1234567890123")
        private val kontor   = Enhetsnummer("1234")
        private val IDENTER  = Identer(brukerId, aktørId)
        private val KONTOR = Kontor(kontor, "Testenhet")
        @ServiceConnection
        private val postgres = postgreSQLContainer
    }
}