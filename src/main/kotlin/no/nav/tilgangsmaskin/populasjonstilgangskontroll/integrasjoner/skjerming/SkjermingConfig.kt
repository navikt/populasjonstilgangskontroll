package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.util.UriBuilder
import java.net.URI

@ConfigurationProperties(SkjermingConfig.Companion.SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    fun skjermetUri(b: UriBuilder) = b.path("skjermet").build()

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val SKJERMING = "skjerming"
        private const val DEFAULT_PING_PATH = ""
    }
}