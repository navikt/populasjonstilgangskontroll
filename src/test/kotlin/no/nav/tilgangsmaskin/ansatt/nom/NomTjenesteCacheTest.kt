package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM_CACHE
import no.nav.tilgangsmaskin.ansatt.nom.NomTjenesteCacheTest.CacheTestNomConfig
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.LocalDate.now

@DataJpaTest
@ActiveProfiles(ClusterConstants.TEST)
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, NomTjeneste::class, NomJPAAdapter::class])
@Import(CacheTestNomConfig::class)
@ApplyExtension(SpringExtension::class)
class NomTjenesteCacheTest : BehaviorSpec() {

    @Configuration
    class CacheTestNomConfig {
        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager(NOM)
        @Bean
        fun cacheOperations(cacheManager: CacheManager): CacheOperations = ConcurrentMapCacheOperations(cacheManager)
    }

    @MockkBean
    private lateinit var token: Token

    @Autowired
    private lateinit var tjeneste: NomTjeneste

    @Autowired
    private lateinit var repo: NomRepository

    @Autowired @Qualifier("cacheOperations")
    private lateinit var cache: CacheOperations

    @Autowired
    private lateinit var cacheManager: CacheManager

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }

    init {
        beforeEach {
            repo.deleteAll()
            cacheManager.getCache(NOM)?.clear()
        }

        val ansattId = AnsattId("Z999999")
        val brukerId = BrukerId("08526835670")

        Given("fnrForAnsatt kalles") {
            When("ansatt finnes i databasen") {
                Then("cache populeres ved første oppslag") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))

                    tjeneste.fnrForAnsatt(ansattId)

                    cache.getOne(NOM_CACHE, ansattId.verdi, BrukerId::class) shouldBe brukerId
                }
            }

            When("fnrForAnsatt kalles to ganger for samme ansatt") {
                Then("returnerer cachet verdi ved andre oppslag") {
                    val id = AnsattId("Z100010")
                    tjeneste.lagre(NomAnsattData(id, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))

                    val first = tjeneste.fnrForAnsatt(id)
                    val second = tjeneste.fnrForAnsatt(id)

                    first shouldBe brukerId
                    second shouldBe brukerId
                    cache.getOne(NOM_CACHE, id.verdi, BrukerId::class) shouldBe brukerId
                }
            }

            When("ansatt ikke finnes i databasen") {
                Then("returnerer null og cacher null") {
                    val id = AnsattId("Z100011")

                    val first = tjeneste.fnrForAnsatt(id)
                    val second = tjeneste.fnrForAnsatt(id)

                    first shouldBe null
                    second shouldBe null
                }
            }

            When("to ulike ansattId-er slås opp") {
                Then("caches separat") {
                    val annenAnsattId = AnsattId("Z888888")
                    val annenBrukerId = BrukerId("20478606614")
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))
                    tjeneste.lagre(NomAnsattData(annenAnsattId, annenBrukerId, NomAnsattPeriode(now(), now().plusYears(1))))

                    tjeneste.fnrForAnsatt(ansattId)
                    tjeneste.fnrForAnsatt(annenAnsattId)

                    cache.getOne(NOM_CACHE, ansattId.verdi, BrukerId::class) shouldBe brukerId
                    cache.getOne(NOM_CACHE, annenAnsattId.verdi, BrukerId::class) shouldBe annenBrukerId
                }
            }

            When("lagre kalles etter cache er populert") {
                Then("cache evictes") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))
                    tjeneste.fnrForAnsatt(ansattId)

                    val nyBrukerId = BrukerId("20478606614")
                    tjeneste.lagre(NomAnsattData(ansattId, nyBrukerId, NomAnsattPeriode(now(), now().plusYears(1))))

                    cache.getOne(NOM_CACHE, ansattId.verdi, BrukerId::class) shouldBe null
                }
            }

            When("lagre kalles med utgått periode") {
                Then("fnrForAnsatt returnerer null") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))
                    tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId

                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now(), now().minusYears(1))))

                    tjeneste.fnrForAnsatt(ansattId) shouldBe null
                }
            }
        }
    }
}