package no.nav.tilgangsmaskin.felles.cache

import org.springframework.stereotype.Component
import org.springframework.context.annotation.Lazy

@Component
@Lazy
class CacheSizeAware(private val cache: CacheOperations, private vararg val cfgs: CachableRestConfig) {
    fun sizes() = cache.sizes(*cfgs.flatMap { it.caches }.toTypedArray())
}
