package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.net.URI

@ConfigurationProperties(OPPFØLGING)
class OppfølgingConfig(baseUri: URI = DEFAULT_URI, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, OPPFØLGING, enabled), CachableRestConfig {

    override val varighet = Duration.ofHours(12)
    override val navn = OPPFØLGING
    companion object {
        private val DEFAULT_URI = URI.create("http://www.todo.com") // TODO
        private const val DEFAULT_PING_PATH = "/internal/health/liveness"
        const val OPPFØLGING = "oppfølging"
    }
}
