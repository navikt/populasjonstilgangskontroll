package no.nav.tilgangsmaskin.felles.cache

import org.springframework.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

class ConcurrentMapCacheOperations(private val mgr: CacheManager) : CacheOperations {

    override fun delete(cache: CacheNøkkelConfig, id: String): Long {
        val c = mgr.getCache(cache.name) ?: return 0L
        val key = tilNøkkel(cache, id)
        val existed = c.get(key) != null
        c.evict(key)
        return if (existed) 1L else 0L
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        mgr.getCache(cache.name)?.get(tilNøkkel(cache, id))?.get() as T?

    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        mgr.getCache(cache.name)?.put(tilNøkkel(cache, id), value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> =
        ids.associateWith { id ->
            mgr.getCache(cache.name)?.get(tilNøkkel(cache, id))?.get() as T?
        }.filterValues { it != null }

    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        val c = mgr.getCache(cache.name) ?: return
        innslag.forEach { (id, value) -> c.put(tilNøkkel(cache, id), value) }
    }

    override fun tilNøkkel(cache: CacheNøkkelConfig, id: String) =
        cache.extraPrefix ?.let { "$it:$id"  } ?: id
}
