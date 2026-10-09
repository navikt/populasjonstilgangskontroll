package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.sikkerhetstjenesten.felles.cache.CachableRestConfig
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkelConfig
import no.nav.sikkerhetstjenesten.felles.rest.RestConfig
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_PING_PATH
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI

@Component
class SkjermingConfig(
    @Value("\${spring.http.serviceclient.skjerming.base-url}")
    baseUrl: URI,
) : CachableRestConfig, RestConfig(baseUrl, SKJERMING_PING_PATH, SKJERMING) {

    override val navn = name
    override val caches = setOf(SKJERMING_CACHE)

    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val SKJERMING = "skjerming"
        val SKJERMING_CACHE = CacheNøkkelConfig(SKJERMING)
    }
}