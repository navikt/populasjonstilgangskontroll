package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.Companion.PING_PATH
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties(VERGEMÅL)
class VergemålConfig : CachableRestConfig, AbstractRestConfig(VERGEMÅL_BASE, PING_PATH, VERGEMÅL) {

    override val navn = VERGEMÅL
    override val caches = setOf(VERGE_CACHE)
    override val varighet = Duration.ofHours(24)

    @Generated
    override fun toString() = "${javaClass.simpleName} [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val VERGEMÅL_BASE = URI.create("http://repr-api.repr")
        val VERGE_CACHE = CachableConfig(VERGEMÅL)
        const val VERGEMÅL = "verge"
    }
}
