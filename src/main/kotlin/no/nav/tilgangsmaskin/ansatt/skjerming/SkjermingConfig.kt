package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.AbstractRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) :
    AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    fun skjermetUri() = builder().path(DEFAULT_SKJERMING_PATH).build()

    fun skjermetBulkUri() = builder().path(DEFAULT_SKJERMING_BULK_PATH).build()

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