package no.nav.tilgangsmaskin.ansatt.skjerming

import java.net.URI
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(SKJERMING)
class SkjermingConfig(baseUri: URI, pingPath: String = DEFAULT_PING_PATH,
                      override val initialCacheSize : Int = 100,
                      override val maxCacheSize : Int  = 50000,
                      override val expireHours : Long = 12,
                      enabled: Boolean = true) : CachableRestConfig,
    AbstractRestConfig(baseUri, pingPath, SKJERMING, enabled) {

    val skjermingUri = uri(DEFAULT_SKJERMING_PATH)
    val skjermingerUri = uri(DEFAULT_SKJERMING_BULK_PATH)

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"
    override val navn = SKJERMING

    companion object {
        const val SKJERMING = "skjerming"
        const val IDENT = "personident"
        const val IDENTER = IDENT + "er"
        private const val DEFAULT_PING_PATH = "internal/health/liveness"
        private const val DEFAULT_SKJERMING_PATH = "skjermet"
        private const val DEFAULT_SKJERMING_BULK_PATH = "skjermetBulk"

    }
}