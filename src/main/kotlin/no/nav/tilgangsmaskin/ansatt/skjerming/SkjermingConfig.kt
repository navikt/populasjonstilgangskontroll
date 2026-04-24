package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingClient.Companion.PING_PATH
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(SKJERMING)
class SkjermingConfig : CachableRestConfig,
    AbstractRestConfig(SKJERMING_BASE, PING_PATH, SKJERMING) {


    override val navn = name
    override val caches = setOf(SKJERMING_CACHE)

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        val SKJERMING_BASE = URI.create("http://skjermede-personer-pip.nom")
        const val SKJERMING = "skjerming"
        val SKJERMING_CACHE = CachableConfig(SKJERMING)
    }


}