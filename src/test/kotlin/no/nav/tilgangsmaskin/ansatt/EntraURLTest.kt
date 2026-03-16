package no.nav.tilgangsmaskin.ansatt

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GEO_PREFIX
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.net.URI

@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(GlobaleGrupperConfig::class)
@ContextConfiguration(classes = [TestApp::class])
@ApplyExtension(SpringExtension::class)
class EntraURLTest : DescribeSpec() {

    @Autowired
    private lateinit var env: Environment

    init {
        describe("grupperURI") {
            it("UUIDene til gruppene kommer i rett formatering") {
                val globaleGrupper = GlobalGruppe.entries.map { env.getProperty(it.property) }.joinToString(",") { "'$it'" }
                val expectedFilter = "id in($globaleGrupper) or $GEO_PREFIX"
                val actualFilter = EntraConfig(baseUri = URI("https://example.com"))
                    .grupperURI("Z999999", true).query
                    .substringAfter("\$filter=").substringBefore("&")

                actualFilter shouldBe expectedFilter
            }
        }
    }
}