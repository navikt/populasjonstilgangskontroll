package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.OAuth2ClientTestConfig.Companion.SERVICE_CLIENT_PREFIX
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.boot.test.util.TestPropertyValues

class RestTjenesteTestContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val values = serviceClientBaseUrls()
            .map { (client, url) -> "$SERVICE_CLIENT_PREFIX.$client.base-url=$url" }
            .toTypedArray()
        TestPropertyValues.of(*values).applyTo(applicationContext)
    }

    companion object {
        private fun serviceClientBaseUrls() = mapOf(
            "pdl" to "http://pdl-api.pdl",
            "pdlpip" to "http://pdl-api.pdl",
            "skjerming" to "http://skjermede-personer-pip.nom",
            "entraproxy" to "http://entra-proxy.sikkerhetstjenesten",
            "graph" to "https://graph.microsoft.com/v1.0",
            "verge" to "http://repr-api.repr"
        )
    }
}
