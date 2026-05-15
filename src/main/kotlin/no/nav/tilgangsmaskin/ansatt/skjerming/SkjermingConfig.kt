package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.SKJERMING_PING_PATH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.stereotype.Component
import java.net.URI

@Component
class SkjermingConfig : CachableRestConfig,
    RestConfig(SKJERMING_BASE, SKJERMING_PING_PATH, SKJERMING) {

    override val navn = name
    override val caches = setOf(SKJERMING_CACHE)

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val SKJERMING_BASE = URI.create("http://skjermede-personer-pip.nom")
        const val SKJERMING = "skjerming"
        val SKJERMING_CACHE = CacheNøkkelConfig(SKJERMING)
    }
}