package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH,
                      enabled: Boolean = true) : CachableRestConfig,
    AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    val skjermingUri = uri(DEFAULT_SKJERMING_PATH)
    val skjermingerUri = uri(DEFAULT_SKJERMING_BULK_PATH)

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"
    override val navn = name

    companion object {
        const val SKJERMING = "skjerming"
        const val IDENT = "personident"
        const val IDENTER = IDENT + "er"
        private const val DEFAULT_PING_PATH = "internal/health/liveness"
        private const val DEFAULT_SKJERMING_PATH = "skjermet"
        private const val DEFAULT_SKJERMING_BULK_PATH = "skjermetBulk"
    }
}