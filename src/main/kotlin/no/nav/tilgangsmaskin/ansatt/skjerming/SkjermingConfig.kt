package no.nav.tilgangsmaskin.ansatt.skjerming

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) :
    AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    val skjermingUri = builder().path(DEFAULT_SKJERMING_PATH).build()

    val skjermingerUri = builder().path(DEFAULT_SKJERMING_BULK_PATH).build()

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val SKJERMING = "skjerming"
        const val IDENT = "personident"
        const val IDENTER = IDENT + "er"
        private const val DEFAULT_PING_PATH = "internal/health/liveness"
        private const val DEFAULT_SKJERMING_PATH = "skjermet"
        private const val DEFAULT_SKJERMING_BULK_PATH = "skjermetBulk"

    }
}