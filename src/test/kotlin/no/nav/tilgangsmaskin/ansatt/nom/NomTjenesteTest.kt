package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkSpyBean
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.ansatt.nom.NomTjenesteCacheTest.CacheTestConfig
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.tilgang.Token
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
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

// ── Unit tests (no Spring context) ──────────────────────────────────────────

class NomTjenesteTest : DescribeSpec({

    val adapter = mockk<NomJPAAdapter>(relaxed = true)
    val tjeneste = NomTjeneste(adapter)

    val ansattId = AnsattId("Z999999")
    val brukerId = BrukerId("08526835670")

    beforeEach { clearMocks(adapter) }

    describe("fnrForAnsatt") {

        it("returnerer fnr fra adapter") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

            tjeneste.fnrForAnsatt(ansattId) shouldBe brukerId
        }

        it("returnerer null når ansatt ikke finnes") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns null

            tjeneste.fnrForAnsatt(ansattId) shouldBe null
        }

        it("delegerer til adapter med riktig verdi") {
            every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

            tjeneste.fnrForAnsatt(ansattId)

            verify(exactly = 1) { adapter.fnrForAnsatt(ansattId.verdi) }
        }
    }

    describe("ryddOpp") {

        it("returnerer antall slettede rader fra adapter") {
            every { adapter.ryddOpp() } returns 3

            tjeneste.ryddOpp() shouldBe 3
        }

        it("returnerer 0 når ingen rader slettes") {
            every { adapter.ryddOpp() } returns 0

            tjeneste.ryddOpp() shouldBe 0
        }

        it("delegerer til adapter") {
            tjeneste.ryddOpp()

            verify(exactly = 1) { adapter.ryddOpp() }
        }
    }

    describe("lagre") {

        it("delegerer til adapter.upsert") {
            val data = NomAnsattData(ansattId, brukerId, NomAnsattData.ALWAYS)

            tjeneste.lagre(data)

            verify(exactly = 1) { adapter.upsert(data) }
        }
    }
})

// ── Cache tests (Spring context with ConcurrentMapCacheManager) ──────────────

@DataJpaTest
@ActiveProfiles(TEST)
@Testcontainers
@EnableCaching
@ContextConfiguration(classes = [TestApp::class, NomTjeneste::class, NomJPAAdapter::class])
@Import(CacheTestConfig::class)
@ApplyExtension(SpringExtension::class)
class NomTjenesteCacheTest : DescribeSpec() {

    @Configuration
    class CacheTestConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(NOM)
    }

    @MockkBean private lateinit var token: Token
    @MockkSpyBean private lateinit var adapter: NomJPAAdapter
    @Autowired private lateinit var tjeneste: NomTjeneste
    @Autowired lateinit var cacheManager: CacheManager

    @BeforeEach
    fun setUp(@Autowired cacheManager: CacheManager) {
        cacheManager.getCache(NOM)?.clear()
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }

    init {
        val brukerId = BrukerId("08526835670")

        describe("fnrForAnsatt — cache") {

            it("populerer cache ved første oppslag") {
                val ansattId = AnsattId("Z100001")
                every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

                tjeneste.fnrForAnsatt(ansattId)

                cacheManager.getCache(NOM)?.get(ansattId.verdi)?.get() shouldBe brukerId
            }

            it("returnerer samme verdi fra cache ved andre oppslag") {
                val ansattId = AnsattId("Z100002")
                every { adapter.fnrForAnsatt(ansattId.verdi) } returns brukerId

                val first = tjeneste.fnrForAnsatt(ansattId)
                val second = tjeneste.fnrForAnsatt(ansattId)

                first shouldBe brukerId
                second shouldBe brukerId
            }

            it("cacher null-resultat slik at null returneres konsistent") {
                val ansattId = AnsattId("Z100003")

                val first = tjeneste.fnrForAnsatt(ansattId)
                val second = tjeneste.fnrForAnsatt(ansattId)

                first shouldBe null
                second shouldBe null
            }

            it("ulike ansattId-er cachet separat") {
                val ansattId1 = AnsattId("Z100004")
                val ansattId2 = AnsattId("Z100005")
                every { adapter.fnrForAnsatt(ansattId1.verdi) } returns brukerId

                tjeneste.fnrForAnsatt(ansattId1)
                tjeneste.fnrForAnsatt(ansattId2)

                cacheManager.getCache(NOM)?.get(ansattId1.verdi)?.get() shouldBe brukerId
                cacheManager.getCache(NOM)?.get(ansattId2.verdi)?.get() shouldBe null
            }
        }
    }
}
