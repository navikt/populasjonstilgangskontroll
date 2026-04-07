package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VERGEMÅL)
class VergemålConfig(
    baseUri: URI = DEFAULT_URI,
    pingPath: String = DEFAULT_PING_PATH,
    enabled: Boolean = true) :  AbstractRestConfig(baseUri, pingPath, VERGEMÅL, enabled) {

    val vergemålURI =
        builder().apply {
            path(DEFAULT_PATH)
        }.build()

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        private val DEFAULT_URI = URI.create("https://repr-api.repr")
        const val VERGEMÅL = "verge"
        private const val DEFAULT_PATH = "/api/v2/internbruker/vergemaal/kan-representere"
        private const val DEFAULT_PING_PATH = "/actuator/health/liveness"
    }
}
