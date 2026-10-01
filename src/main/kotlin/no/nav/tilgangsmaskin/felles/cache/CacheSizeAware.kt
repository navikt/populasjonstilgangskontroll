package no.nav.tilgangsmaskin.felles.cache

import no.nav.sikkerhetstjenesten.felles.cache.CachableRestConfig
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import org.springframework.stereotype.Component

@Component
class CacheSizeAware(private val cache: CacheOperations, private vararg val cfgs: CachableRestConfig) {
    fun sizes() = cache.sizes(*cfgs.flatMap { it.caches }.toTypedArray())
}
