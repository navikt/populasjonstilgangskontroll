package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig

class CacheSizeAware(private val cache: CacheOperations, private vararg val cfgs: CachableRestConfig) {
    fun sizes() = cache.sizes(*cfgs.flatMap { it.caches }.toTypedArray())
}
