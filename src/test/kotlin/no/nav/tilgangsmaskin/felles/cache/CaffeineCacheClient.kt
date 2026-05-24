package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

class CaffeineCacheClient(private val cacheManager: CacheManager) : CacheOperations {

    private val log = getLogger(javaClass)

    override fun delete(cache: CacheNøkkelConfig, id: String): Long {
        val key = tilNøkkel(cache, id)
        val springCache = cacheManager.getCache(cache.name) ?: return 0L
        val existed = springCache.get(key) != null
        springCache.evict(key)
        return if (existed) 1L else 0L
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        cacheManager.getCache(cache.name)?.get(tilNøkkel(cache, id))?.get() as T?

    override fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) {
        cacheManager.getCache(cache.name)?.put(tilNøkkel(cache, id), value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> {
        if (ids.isEmpty()) return emptyMap()
        val springCache = cacheManager.getCache(cache.name) ?: return emptyMap()
        return ids.associateWith { id ->
            springCache.get(tilNøkkel(cache, id))?.get() as T?
        }.filterValues { it != null }
    }

    override fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        if (innslag.isEmpty()) return
        val springCache = cacheManager.getCache(cache.name) ?: return
        log.trace("Caffeine bulk lagrer {} verdier for cache {}", innslag.size, cache.name)
        innslag.forEach { (id, value) -> springCache.put(tilNøkkel(cache, id), value) }
    }

    override fun tilNøkkel(cache: CacheNøkkelConfig, id: String): String {
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$extra$id"
    }

    override fun clear(cache: CacheNøkkelConfig) {
        val springCache = cacheManager.getCache(cache.name) ?: return
        if (cache.extraPrefix == null) {
            springCache.clear()
        } else {
            val prefix = tilNøkkel(cache, "")
            val nativeCache = springCache.nativeCache
            if (nativeCache is com.github.benmanes.caffeine.cache.Cache<*, *>) {
                nativeCache.asMap().keys
                    .filterIsInstance<String>()
                    .filter { it.startsWith(prefix) }
                    .forEach { springCache.evict(it) }
            }
        }
    }
}
