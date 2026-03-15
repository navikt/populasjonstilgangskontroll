package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkSpyBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
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
import org.junit.jupiter.api.BeforeEach
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
import java.util.UUID.randomUUID

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, OppfølgingTjeneste::class, OppfølgingJPAAdapter::class])
@Import(CacheTestConfig::class)
@ApplyExtension(SpringExtension::class)
class OppfølgingTjenesteTest : DescribeSpec() {

    @Configuration
    class CacheTestConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(OPPFØLGING)
        @Bean fun cacheOperations(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
    }

    @MockkBean private lateinit var token: Token

    @MockkSpyBean private lateinit var adapter: OppfølgingJPAAdapter
    @Autowired private lateinit var tjeneste: OppfølgingTjeneste

    @Qualifier("cacheOperations")
    @Autowired private lateinit var cache: CacheOperations

    @BeforeEach
    fun setUp(@Autowired cacheManager: CacheManager) {
        cacheManager.getCache(OPPFØLGING)?.clear()
    }

    init {
        describe("enhetFor") {

            it("returnerer null når det ikke finnes oppfølging") {
                tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe null
                verify { adapter.enhetFor(brukerId.verdi) }
            }

            it("cacher resultatet etter første oppslag") {
                tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                tjeneste.enhetFor(Identifikator(brukerId.verdi))
                verify(exactly = 0) { adapter.enhetFor(brukerId.verdi) }
                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
            }

            it("kaller ikke adapter ved cache-treff") {
                tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe kontor
            }
        }

        describe("registrer") {

            it("populerer cache for brukerId") {
                tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
            }

            it("populerer cache for aktørId") {
                tjeneste.registrer(randomUUID(), IDENTER, KONTOR)
                cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe kontor
            }
        }

        describe("avslutt") {

            it("fjerner cache-innslag for brukerId og aktørId") {
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