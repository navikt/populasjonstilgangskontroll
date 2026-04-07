package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(VERGEMÅL)
class VergemålConfig(
    baseUri: URI,
    val path: String = DEFAULT_PATH,
    pingPath: String = DEFAULT_PING_PATH,
    enabled: Boolean = true) :  AbstractRestConfig(baseUri, pingPath, VERGEMÅL, enabled) {

    val vergemålURI =
        builder().apply {
            path(path)
        }.build()

    @Generated
    override fun toString() = "$javaClass.simpleName [uri=$vergemålURI, pingEndpoint=$pingEndpoint]"

    companion object {
        const val VERGEMÅL = "verge"
        private const val DEFAULT_PATH = "/api/v2/internbruker/vergemaal/kan-representere"
        private const val DEFAULT_PING_PATH = "/actuator/health/liveness"
    }
}
