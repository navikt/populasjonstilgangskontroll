package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GEO_PREFIX
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig
import no.nav.tilgangsmaskin.regler.motor.GlobaleGrupperConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.core.env.Environment
import java.net.URI
@RestClientTest
@TestPropertySource(locations = ["classpath:test.properties"])
@EnableConfigurationProperties(GlobaleGrupperConfig::class)
@ContextConfiguration(classes = [TestApp::class])
class EntraUrl {

    @Autowired
    private lateinit var env: Environment

    @Test
    @DisplayName("UUIDene til gruppene kommer i rett formatering")
    fun `GrupperCcfURI should correctly format PARAM_NAME_FILTER`() {
        val globaleGrupper = GlobalGruppe.entries.map { env.getProperty(it.property) }.joinToString(",") { "'$it'" }
        val expectedFilter = "id in($globaleGrupper) or $GEO_PREFIX"
        val actualFilter = EntraConfig(baseUri = URI("https://example.com")).grupperURI("Z999999", true).query.substringAfter("\$filter=").substringBefore("&")
        assertEquals(expectedFilter, actualFilter, "PARAM_NAME_FILTER is not formatted correctly")
    }
}