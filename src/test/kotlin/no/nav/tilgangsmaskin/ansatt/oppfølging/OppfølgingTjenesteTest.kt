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
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjenesteTest.CacheTestConfig
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.tilgang.Token


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.util.UUID.*

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, OppfølgingTjeneste::class, OppfølgingJPAAdapter::class])
@Import(CacheTestConfig::class)
@ApplyExtension(SpringExtension::class)
class OppfølgingTjenesteTest : BehaviorSpec() {

    @Configuration
    class CacheTestConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(OPPFØLGING)
        @Bean fun cacheOperations(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
    }

    @MockkBean private lateinit var token: Token

    @MockkSpyBean private lateinit var adapter: OppfølgingJPAAdapter
    @Autowired private lateinit var tjeneste: OppfølgingTjeneste
    @Autowired private lateinit var cacheManager: CacheManager

    @Qualifier("cacheOperations")
    @Autowired private lateinit var cache: CacheOperations

    init {
        beforeEach { cacheManager.getCache(OPPFØLGING)?.clear() }

        Given("enhetFor") {
            When("det ikke finnes oppfølging") {
                Then("returneres null") {
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe null
                    verify { adapter.enhetFor(brukerId.verdi) }
                }
            }

            When("oppfølging er registrert") {
                Then("caches resultatet etter første oppslag") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    tjeneste.enhetFor(Identifikator(brukerId.verdi))
                    verify(exactly = 0) { adapter.enhetFor(brukerId.verdi) }
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                }
                Then("returneres enhet ved cache-treff uten adapter-kall") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe kontor
                }
            }
        }

        Given("registrer") {
            When("registrering utføres") {
                Then("populeres cache for brukerId") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                }
                Then("populeres cache for aktørId") {
                    tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe kontor
                }
            }
        }

        Given("avslutt") {
            When("avslutt kalles etter registrering") {
                Then("fjernes cache-innslag for brukerId og aktørId") {
                    val id = randomUUID()
                    tjeneste.registrer(id, IDENTER, KONTOR)
                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi,  Enhetsnummer::class) shouldBe kontor

                    tjeneste.avslutt(id, IDENTER)

                    cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe null
                    cache.getOne(OPPFØLGING_CACHE, aktørId.verdi,  Enhetsnummer::class) shouldBe null
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
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}