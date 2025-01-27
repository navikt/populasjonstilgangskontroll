package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.util.UriBuilder
import java.net.URI

@ConfigurationProperties(SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    fun skjermetUri(b: UriBuilder) = b.path(DEFAULT_SKJERMING_PATH).build()

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val SKJERMING = "skjerming"
        const val IDENT = "personident"
        private const val DEFAULT_PING_PATH = "internal/health/liveness"
        private const val DEFAULT_SKJERMING_PATH = "skjermet"
    }
}