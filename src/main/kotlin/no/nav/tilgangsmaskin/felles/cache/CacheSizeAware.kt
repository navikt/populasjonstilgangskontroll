package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig

class CacheSizeAware(private val cfgs: Set<CachableRestConfig>, private val cache: CacheOperations) {
    fun sizes() =
        cache.sizes(cfgs.flatMap { it.caches}.toSet())

}