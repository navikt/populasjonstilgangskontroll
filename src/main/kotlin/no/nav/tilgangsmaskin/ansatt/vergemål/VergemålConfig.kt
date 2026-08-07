package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.Companion.VERGEMÅL_PING_PATH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.time.Duration

@Component
class VergemålConfig(@Value("\${spring.http.serviceclient.verge.base-url}") baseUrl: URI) : CachableRestConfig, RestConfig(baseUrl, VERGEMÅL_PING_PATH, VERGEMÅL) {

    override val navn = VERGEMÅL
    override val caches = setOf(VERGE_CACHE)
    override val varighet = Duration.ofHours(24)

    override fun toString() =
        "${javaClass.simpleName} [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val VERGE_CACHE = CacheNøkkelConfig(VERGEMÅL)
        const val VERGEMÅL = "verge"
    }
}
