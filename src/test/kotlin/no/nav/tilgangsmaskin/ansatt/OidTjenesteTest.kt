package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.OidTjenesteTest.EntraTestConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidBeanConfig
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient.Companion.ENTRA_USERS_PATH
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidException
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidTjeneste
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.ConcurrentMapCacheOperations
import org.hamcrest.Matchers.containsString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.util.UUID

@RestClientTest(components = [EntraOidTjeneste::class, EntraGrupperConfig::class, EntraOidBeanConfig::class])
@Import(EntraTestConfig::class)
@ApplyExtension(SpringExtension::class)
class OidTjenesteTest : BehaviorSpec() {

    @TestConfiguration
    @EnableCaching(proxyTargetClass = true)
    class EntraTestConfig {
        @Bean
        fun cacheManager() =
            ConcurrentMapCacheManager(ENTRA_OID)

        @Bean
        fun cacheOperations(cacheManager: CacheManager) = ConcurrentMapCacheOperations(cacheManager)
    }

    @Autowired
    private lateinit var tjeneste: EntraOidTjeneste
    @Autowired
    private lateinit var server: MockRestServiceServer
    @Autowired
    private lateinit var cacheManager: CacheManager
    @Autowired
    private lateinit var cache: CacheOperations

    init {
        beforeEach {
            server.reset()
            cacheManager.getCache(ENTRA_OID)?.clear()
        }

        afterEach {
            server.verify()
        }

        Given("oidFraEntra") {
            When("ansatt har én oid i Entra") {
                Then("returneres oid") {
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(OID), APPLICATION_JSON))

                    tjeneste.oid(ANSATTID) shouldBe OID
                }
            }

            When("samme ansatt slås opp to ganger") {
                Then("REST kalles kun én gang — andre svar returneres fra cache") {
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(OID), APPLICATION_JSON))

                    tjeneste.oid(ANSATTID) shouldBe OID
                    tjeneste.oid(ANSATTID) shouldBe OID
                }
            }

            When("ingen oid finnes i Entra") {
                Then("kastes EntraOidException") {
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess("""{"value": []}""", APPLICATION_JSON))

                    shouldThrow<EntraOidException> { tjeneste.oid(ANSATTID) }
                }
            }

            When("flere oids finnes for samme ansatt") {
                Then("kastes EntraOidException") {
                    val oid2 = UUID.randomUUID()
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(OID, oid2), APPLICATION_JSON))

                    shouldThrow<EntraOidException> { tjeneste.oid(ANSATTID) }
                }
            }
        }

        Given("cache") {
            When("oid kalles to ganger for samme ansatt") {
                Then("server kalles kun én gang og oid er i cache") {
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(OID), APPLICATION_JSON))

                    tjeneste.oid(ANSATTID) shouldBe OID
                    tjeneste.oid(ANSATTID) shouldBe OID
                    cache.getOne(OID_CACHE, ANSATTID.verdi, UUID::class) shouldBe OID
                }
            }

            When("to ulike ansatte slås opp") {
                Then("caches separat — server kalles to ganger") {
                    val ansattId2 = AnsattId("Z888888")
                    val oid2 = UUID.randomUUID()
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(OID), APPLICATION_JSON))
                    server.expect(requestTo(containsString(ENTRA_USERS_PATH)))
                        .andExpect(method(GET))
                        .andRespond(withSuccess(oidRespons(oid2), APPLICATION_JSON))

                    tjeneste.oid(ANSATTID)  shouldBe OID
                    tjeneste.oid(ansattId2) shouldBe oid2
                    cache.getOne(OID_CACHE, ANSATTID.verdi,  UUID::class) shouldBe OID
                    cache.getOne(OID_CACHE, ansattId2.verdi, UUID::class) shouldBe oid2
                }
            }
        }
    }

    private fun oidRespons(vararg ids: UUID) =
        """
        {"value": [${ids.joinToString(",") { """{"id": "$it"}""" }}]}
        """.trimIndent()

    private companion object {
        private val ANSATTID = AnsattId("Z999999")
        private val OID = UUID.randomUUID()
    }
}
