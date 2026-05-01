package no.nav.tilgangsmaskin.ansatt.oppfølging

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
import no.nav.tilgangsmaskin.felles.cache.AbstractCacheTestConfig
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.util.UUID.*

@DataJpaTest
@EnableJpaAuditing
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, OppfølgingTjeneste::class, OppfølgingJPAAdapter::class])
@Import(OppfølgingTestConfig::class)
@ApplyExtension(SpringExtension::class)
class OppfølgingTjenesteTest : BehaviorSpec() {

    @MockkBean
    private lateinit var token: Token

    @MockkSpyBean
    private lateinit var adapter: OppfølgingJPAAdapter

    @Autowired
    private lateinit var tjeneste: OppfølgingTjeneste

    @Autowired
    private lateinit var cache: CacheOperations

    @Autowired
    private lateinit var cacheManager: CacheManager

    @TestConfiguration
    @EnableCaching
    @EnableResilientMethods
    class OppfølgingTestConfig: AbstractCacheTestConfig(OPPFØLGING)

    init {
        beforeEach { cacheManager.getCache(OPPFØLGING)?.clear() }

        Given("enhetFor") {
            When("ingen oppfølging er registrert") {
                Then("returnerer null og kaller adapter") {
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe null
                    verify { adapter.enhetFor(brukerId.verdi) }
                }
            }

            When("oppfølging er registrert") {
                Then("cacher resultatet — adapter kalles ikke ved andre oppslag") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    tjeneste.enhetFor(Identifikator(brukerId.verdi))
                    verify(exactly = 0) { adapter.enhetFor(brukerId.verdi) }
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                }

                Then("returnerer cachet kontor") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe kontor
                }
            }
        }

        Given("registrer") {
            When("kalles med identer og kontor") {
                Then("populerer cache for brukerId") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                }

                Then("populerer cache for aktørId") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe kontor
                }
            }
        }

        Given("avslutt") {
            When("kalles etter registrer") {
                Then("fjerner cache-innslag for brukerId og aktørId") {
                    val id = randomUUID()
                    tjeneste.registrer(id, IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe kontor

                    tjeneste.avslutt(id, IDENTER)

                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe null
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe null
                }
            }
        }
    }

    companion object {
        private val brukerId = BrukerId("08526835670")
        private val aktørId  = AktørId("1234567890123")
        private val kontor   = Enhetsnummer("1234")
        private val IDENTER  = Identer(brukerId, aktørId)
        private val KONTOR   = Kontor(kontor, "Testenhet")
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}