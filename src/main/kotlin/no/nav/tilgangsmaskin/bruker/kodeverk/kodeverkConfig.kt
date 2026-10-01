package no.nav.tilgangsmaskin.bruker.kodeverk

import no.nav.sikkerhetstjenesten.felles.cache.CachableRestConfig
import no.nav.sikkerhetstjenesten.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkClient.Companion.KODEVERK_PING_PATH
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI

@Component
class KodeverkConfig(@Value($$"${spring.http.serviceclient.kodeverk.base-url}") baseUrl: URI) : RestConfig(baseUrl, KODEVERK_PING_PATH, KODEVERK), CachableRestConfig {
    override val navn = KODEVERK
    override val caches = setOf(KODEVERK_CACHE)

    companion object {
        const val KODEVERK  = "kodeverk"
        private val KODEVERK_CACHE = CacheNøkkelConfig(KODEVERK)
    }
}