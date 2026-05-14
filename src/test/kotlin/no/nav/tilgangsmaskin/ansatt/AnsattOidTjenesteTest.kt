package no.nav.tilgangsmaskin.ansatt

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjenesteTest.EntraTestConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.ansatt.graph.EntraClient
import no.nav.tilgangsmaskin.ansatt.graph.EntraOidRespons
import no.nav.tilgangsmaskin.ansatt.graph.EntraOidRespons.EntraOid
import no.nav.tilgangsmaskin.ansatt.graph.EntraOidException
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import java.util.UUID

@ContextConfiguration(classes = [EntraRestClientAdapter::class, AnsattOidTjeneste::class, EntraConfig::class])
@Import(EntraTestConfig::class)
@ApplyExtension(SpringExtension::class)
class AnsattOidTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching(proxyTargetClass = true)
    class EntraTestConfig {
        @Bean
        fun cacheManager() =
            ConcurrentMapCacheManager(ENTRA_OID)


        @Bean
        @Qualifier(GRAPH)
        fun graphRestClient() = RestClient.builder().build()
    }

    @MockkBean
    lateinit var entraClient: EntraClient
    @Autowired
    lateinit var tjeneste: AnsattOidTjeneste
    @Autowired
    lateinit var cacheManager: CacheManager

    private val ansattId = AnsattId("Z999999")
    private val oid = UUID.randomUUID()
    private val filter = "onPremisesSamAccountName eq '${ansattId.verdi}'"

    init {
        beforeEach {
            cacheManager.getCache(ENTRA_OID)?.clear()
        }

        Given("oidFraEntra") {
            When("ansatt har én oid i Entra") {
                Then("returneres oid") {
                    every { entraClient.oid(filter) } returns
                        EntraOidRespons(setOf(EntraOid(oid)))

                    tjeneste.oid(ansattId) shouldBe oid
                }
            }

            When("samme ansatt slås opp to ganger") {
                Then("REST kalles kun én gang — andre svar returneres fra cache") {
                    every { entraClient.oid(filter) } returns
                        EntraOidRespons(setOf(EntraOid(oid)))

                    tjeneste.oid(ansattId) shouldBe oid
                    tjeneste.oid(ansattId) shouldBe oid

                    verify { entraClient.oid(filter, "id") }
                }
            }

            When("ingen oid finnes i Entra") {
                Then("kastes EntraOidException") {
                    every { entraClient.oid(filter) } returns
                        EntraOidRespons(emptySet())

                    shouldThrow<EntraOidException> { tjeneste.oid(ansattId) }
                }
            }

            When("flere oids finnes for samme ansatt") {
                Then("kastes EntraOidException") {
                    val oid2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
                    every { entraClient.oid(filter) } returns
                        EntraOidRespons(setOf(EntraOid(oid), EntraOid(oid2)))

                    shouldThrow<EntraOidException> { tjeneste.oid(ansattId) }
                }
            }
        }
    }
}
