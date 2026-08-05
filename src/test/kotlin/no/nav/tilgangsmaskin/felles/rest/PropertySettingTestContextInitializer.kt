package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.OAuth2ClientTestConfig.Companion.SERVICE_CLIENT_PREFIX
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.test.context.support.TestPropertySourceUtils.addPropertiesFilesToEnvironment

class PropertySettingTestContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(ctx: ConfigurableApplicationContext) {
        addPropertiesFilesToEnvironment(ctx, "classpath:test.properties")
        val values = serviceClientBaseUrls()
            .map { (client, url) -> "$SERVICE_CLIENT_PREFIX.$client.base-url=$url" }
            .toTypedArray()
        TestPropertyValues.of(*values).applyTo(ctx)
    }

    companion object {
        private fun serviceClientBaseUrls() = mapOf(
            "pdlpip" to "http://pdlpip.pdl",
            "pdlgraph" to "http://pdlgraph.pdl",
            "skjerming" to "http://skjermede-personer-pip.nom",
            "entraproxy" to "http://entra-proxy.sikkerhetstjenesten",
            "graph" to "https://graph.microsoft.com/v1.0",
            "verge" to "http://repr-api.repr"
        )
    }
}
