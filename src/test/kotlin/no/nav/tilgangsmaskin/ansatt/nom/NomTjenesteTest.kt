package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkSpyBean
import io.mockk.verify
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM_CACHE
import no.nav.tilgangsmaskin.ansatt.nom.NomTjenesteTest.NomTestConfig
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.LocalDate.now

@DataJpaTest
@Testcontainers
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(value = [GlobaleGrupperConfig::class])
@ContextConfiguration(classes = [TestApp::class,NomTjeneste::class, NomJPAAdapter::class])
@Import(NomTestConfig::class)
@ApplyExtension(SpringExtension::class)
class NomTjenesteTest : BehaviorSpec() {

    @EnableCaching
    @Configuration
    class NomTestConfig {
        @Bean
        fun cacheManager() =
            ConcurrentMapCacheManager(NOM)

        @Bean
        fun cacheOperations(cacheManager: CacheManager) = ConcurrentMapCacheOperations(cacheManager)
    }

    @MockkBean
    private lateinit var token: Token
    @Autowired
    private lateinit var tjeneste: NomTjeneste
    @Autowired
    private lateinit var repo: NomRepository
    @Autowired
    private lateinit var cacheManager: CacheManager
    @Autowired
    @Qualifier("cacheOperations")
    private lateinit var cache: CacheOperations
    @MockkSpyBean
    private lateinit var adapter: NomJPAAdapter

    private val ansattId = AnsattId("Z999999")
    private val brukerId = BrukerId("08526835670")

    init {
        beforeEach {
            every { token.system } returns "test"
            repo.deleteAll()
            cacheManager.getCache(NOM)?.clear()
        }


        Given("NomTjeneste — fnrForAnsatt") {
            When("ansatt ikke finnes i databasen") {
                Then("returneres null") {
                    tjeneste.fnrForAnsatt(AnsattId("X000000")) shouldBe null
                }
            }

            When("ansatt finnes i databasen") {
                Then("returneres fnr") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId))
                    tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId
                }
            }
        }

        Given("NomTjeneste — lagre") {
            When("eksisterende ansatt oppdateres") {
                Then("nyeste fnr returneres") {
                    val nyttBrukerId = BrukerId("12345678901")
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId))
                    tjeneste.lagre(NomAnsattData(ansattId, nyttBrukerId))
                    tjeneste.fnrForAnsatt(ansattId) shouldBe nyttBrukerId
                }
            }
        }

        Given("NomTjeneste — ryddOpp") {
            When("ingen utgåtte ansatte") {
                Then("returnerer 0") {
                    tjeneste.ryddOpp() shouldBe 0
                }
            }

            When("én utgått ansatt finnes") {
                Then("slettes og antall returneres") {
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now().minusYears(2), now().minusYears(1))))
                    tjeneste.ryddOpp() shouldBe 1
                    tjeneste.fnrForAnsatt(ansattId) shouldBe null
                }
            }

            When("én gyldig og én utgått ansatt finnes") {
                Then("kun utgått slettes") {
                    val annenAnsattId = AnsattId("Z777777")
                    tjeneste.lagre(NomAnsattData(ansattId, brukerId, NomAnsattPeriode(now().minusYears(2), now().minusYears(1))))
                    tjeneste.lagre(NomAnsattData(annenAnsattId, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))
                    tjeneste.ryddOpp() shouldBe 1
                    tjeneste.fnrForAnsatt(annenAnsattId) shouldBe brukerId
                    tjeneste.fnrForAnsatt(ansattId) shouldBe null
                }
            }
        }

        Given("cache") {

            When("fnrForAnsatt kalles to ganger for samme ansatt") {
                Then("returnerer cachet verdi ved andre oppslag") {
                    val id = AnsattId("Z100010")
                    tjeneste.lagre(NomAnsattData(id, brukerId, NomAnsattPeriode(now(), now().plusYears(1))))
                    tjeneste.fnrForAnsatt(id) shouldBe brukerId
                    tjeneste.fnrForAnsatt(id) shouldBe brukerId
                    cache.getOne(NOM_CACHE, id.verdi, BrukerId::class) shouldBe brukerId
                    verify(exactly = 1) { adapter.fnrForAnsatt(id.verdi) }
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

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:18")
    }
}
