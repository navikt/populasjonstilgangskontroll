package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration
import java.util.UUID

@ConfigurationProperties(OPPFØLGING)
class OppfølgingConfig(baseUri: URI = DEFAULT_BASE_URI, bulkPath : String = DEFAULT_BULK_PATH, pingPath: String = DEFAULT_PING_PATH, enabled: Boolean = true) : AbstractRestConfig(baseUri, pingPath, OPPFØLGING, enabled), CachableRestConfig {

    override val varighet = Duration.ofHours(12)
    override val navn = OPPFØLGING
    override val caches = setOf(OPPFØLGING_CACHE)
    override val clazz = Enhetsnummer::class

    val bulkUri = uri(bulkPath)

    companion object {
        private val DEFAULT_BULK_PATH = "/api/tilgang/brukers-kontor-bulk"
        private val DEFAULT_BASE_URI = URI.create("http://ao-oppfolgingskontor.dab")
        private const val DEFAULT_PING_PATH = "/isAlive"
        const val OPPFØLGING = "oppfolging"
        val OPPFØLGING_CACHE = CachableConfig(OPPFØLGING)
    }
}
