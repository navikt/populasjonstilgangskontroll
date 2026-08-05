package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.OAuth2ClientTestConfig.Companion.SERVICE_CLIENT_PREFIX
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.boot.test.util.TestPropertyValues

class PropertySettingTestContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(ctx: ConfigurableApplicationContext) {
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
            "verge" to "http://repr-api.repr",
            "gruppe.strengt" to "5ef775f2-61f8-4283-bf3d-8d03f428aa14",
            "gruppe.nasjonal" to "c7107487-310d-4c06-83e0-cf5395dc3be3",
            "gruppe.utland" to "de62a4bf-957b-4cde-acdb-6d8bcbf821a0",
            "gruppe.udefinert" to "35d9d1ac-7fcb-4a22-9155-e0d1e57898a8",
            "gruppe.fortrolig" to "ea930b6b-9397-44d9-b9e6-f4cf527a632a",
            "gruppe.egenansatt" to "dbe4ad45-320b-4e9a-aaa1-73cca4ee124d",
            "gruppe.dead" to "309f73cc-a76e-4679-a242-9ec0b0424c74"
        )
    }
}
