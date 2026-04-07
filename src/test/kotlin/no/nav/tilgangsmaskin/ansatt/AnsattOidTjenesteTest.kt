package no.nav.tilgangsmaskin.ansatt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.EntraClientBeanConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.util.UUID

@RestClientTest(components = [EntraRestClientAdapter::class, EntraClientBeanConfig::class, AnsattOidTjeneste::class])
@EnableConfigurationProperties(EntraConfig::class)
@Import(AnsattOidTjenesteTest.CacheConfig::class)
@TestPropertySource(properties = ["graph.base-uri=http://graph"])
@ApplyExtension(SpringExtension::class)
class AnsattOidTjenesteTest : DescribeSpec() {

    @TestConfiguration
    @EnableCaching(proxyTargetClass = true)
    class CacheConfig {
        @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager(ENTRA_OID)
    }

    @Autowired lateinit var tjeneste: AnsattOidTjeneste
    @Autowired lateinit var server: MockRestServiceServer
    @Autowired lateinit var cfg: EntraConfig
    @Autowired lateinit var cacheManager: CacheManager

    private val ansattId = AnsattId("Z999999")
    private val oid = UUID.fromString("11111111-1111-1111-1111-111111111111")

    private fun oidRespons(vararg ids: UUID) = """{ "value": [${ids.joinToString(",") { """{ "id": "$it" }""" }}] }"""

    init {
        beforeEach {
            server.reset()
            cacheManager.getCache(ENTRA_OID)?.clear()
        }

        describe("oidFraEntra") {

            it("returnerer oid for gyldig ansatt") {
                server.expect(requestTo(cfg.userURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess(oidRespons(oid), APPLICATION_JSON))

                tjeneste.oidFraEntra(ansattId) shouldBe oid
                server.verify()
            }

            it("cacher resultatet slik at REST ikke kalles på nytt for samme ansatt") {
                server.expect(requestTo(cfg.userURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess(oidRespons(oid), APPLICATION_JSON))

                tjeneste.oidFraEntra(ansattId) shouldBe oid
                tjeneste.oidFraEntra(ansattId) shouldBe oid
                server.verify()
            }

            it("kaster EntraOidException når ingen oid finnes i Entra") {
                server.expect(requestTo(cfg.userURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess("""{ "value": [] }""", APPLICATION_JSON))

                shouldThrow<Exception> { tjeneste.oidFraEntra(ansattId) }
                server.verify()
            }

            it("kaster EntraOidException når flere oids finnes for samme ansatt") {
                val oid2 = UUID.fromString("22222222-2222-2222-2222-222222222222")
                server.expect(requestTo(cfg.userURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withSuccess(oidRespons(oid, oid2), APPLICATION_JSON))

                shouldThrow<Exception> { tjeneste.oidFraEntra(ansattId) }
                server.verify()
            }

            it("propagerer feil fra Entra") {
                server.expect(requestTo(cfg.userURI(ansattId.verdi)))
                    .andExpect(method(GET))
                    .andRespond(withServerError())

                shouldThrow<Exception> { tjeneste.oidFraEntra(ansattId) }
                server.verify()
            }
        }
    }
}

