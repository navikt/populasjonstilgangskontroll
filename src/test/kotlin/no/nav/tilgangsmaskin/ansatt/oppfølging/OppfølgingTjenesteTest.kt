package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING_CACHE
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
import java.util.UUID

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
        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager(OPPFØLGING)

        @Bean
        fun cacheOperations(cacheManager: CacheManager)  =
            ConcurrentMapCacheOperations(cacheManager)
    }

    @MockkBean private lateinit var token: Token

    @Autowired private lateinit var adapter: OppfølgingJPAAdapter

    @Qualifier("cacheOperations")
    @Autowired private lateinit var cache: CacheOperations

    @Autowired private lateinit var tjeneste: OppfølgingTjeneste

    private val brukerId = BrukerId("08526835670")
    private val aktørId  = AktørId("1234567890123")
    private val kontor   = Enhetsnummer("1234")
    private val identer  = Identer(brukerId, aktørId)

    @BeforeEach
    fun clearCache(@Autowired cacheManager: CacheManager) {
        cacheManager.getCache(OPPFØLGING)?.clear()
    }

    init {
        describe("enhetFor") {

            it("returnerer null når det ikke finnes oppfølging") {
                tjeneste.enhetFor(Identifikator(brukerId.verdi)) shouldBe null
            }

            it("cacher resultatet etter første oppslag") {
                tjeneste.registrer(UUID.randomUUID(), identer, OppfølgingHendelse.Kontor(kontor, "Testenhet"))
                tjeneste.enhetFor(Identifikator(brukerId.verdi))
                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
            }
        }

        describe("registrer") {

            it("populerer cache for brukerId") {
                tjeneste.registrer(UUID.randomUUID(), identer, OppfølgingHendelse.Kontor(kontor, "Testenhet"))
                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
            }

            it("populerer cache for aktørId") {
                tjeneste.registrer(UUID.randomUUID(), identer, OppfølgingHendelse.Kontor(kontor, "Testenhet"))
                cache.getOne(OPPFØLGING_CACHE, aktørId.verdi, Enhetsnummer::class) shouldBe kontor
            }
        }

        describe("avslutt") {

            it("fjerner cache-innslag for brukerId og aktørId") {
                val id = UUID.randomUUID()
                tjeneste.registrer(id, identer, OppfølgingHendelse.Kontor(kontor, "Testenhet"))
                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe kontor
                cache.getOne(OPPFØLGING_CACHE, aktørId.verdi,  Enhetsnummer::class) shouldBe kontor

                tjeneste.avslutt(id, identer)

                cache.getOne(OPPFØLGING_CACHE, brukerId.verdi, Enhetsnummer::class) shouldBe null
                cache.getOne(OPPFØLGING_CACHE, aktørId.verdi,  Enhetsnummer::class) shouldBe null
            }
        }
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}