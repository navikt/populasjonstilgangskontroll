package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.Companion.VERGEMÅL_PING_PATH
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties(VERGEMÅL)
class VergemålConfig(val scope: String) : CachableRestConfig, RestConfig(VERGEMÅL_BASE, VERGEMÅL_PING_PATH, VERGEMÅL) {

    override val navn = VERGEMÅL
    override val caches = setOf(VERGE_CACHE)
    override val varighet = Duration.ofHours(24)

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val VERGEMÅL_BASE = URI.create("http://repr-api.repr")
        val VERGE_CACHE = CacheNøkkelConfig(VERGEMÅL)
        const val VERGEMÅL = "verge"
    }
}
