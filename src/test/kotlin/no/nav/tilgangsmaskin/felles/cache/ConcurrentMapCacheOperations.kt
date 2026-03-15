package no.nav.tilgangsmaskin.felles.cache

import org.springframework.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

class ConcurrentMapCacheOperations(private val cacheManager: CacheManager) : CacheOperations {

    override fun delete(cache: CachableConfig, id: String): Long {
        val c = cacheManager.getCache(cache.name) ?: return 0L
        val key = tilNøkkel(cache, id)
        val existed = c.get(key) != null
        c.evict(key)
        return if (existed) 1L else 0L
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getOne(cache: CachableConfig, id: String, clazz: KClass<T>): T? =
        cacheManager.getCache(cache.name)?.get(tilNøkkel(cache, id))?.get() as T?

    override fun putOne(cache: CachableConfig, id: String, value: Any, ttl: Duration) {
        cacheManager.getCache(cache.name)?.put(tilNøkkel(cache, id), value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getMany(cache: CachableConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> =
        ids.associateWith { id ->
            cacheManager.getCache(cache.name)?.get(tilNøkkel(cache, id))?.get() as T?
        }.filterValues { it != null }

    override fun putMany(cache: CachableConfig, innslag: Map<String, Any>, ttl: Duration) {
        val c = cacheManager.getCache(cache.name) ?: return
        innslag.forEach { (id, value) -> c.put(tilNøkkel(cache, id), value) }
    }

    // Matches CacheNøkkelHandler format: "cacheName::extraPrefix:id" or "cacheName::id"
    override fun tilNøkkel(cache: CachableConfig, id: String) = id
}
