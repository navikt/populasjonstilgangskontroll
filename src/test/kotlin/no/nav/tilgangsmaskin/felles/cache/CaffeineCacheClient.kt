package no.nav.tilgangsmaskin.felles.cache

import com.github.benmanes.caffeine.cache.Cache
import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

@ConditionalOnLocalOrTest
class CaffeineCacheClient(private val mgr: CacheManager) : CacheOperations {

    private val log = getLogger(javaClass)

    override fun delete(cfg: CacheNøkkelConfig, id: String): Long {
        val cache = cache(cfg)
        return with(tilNøkkel(cfg, id)) {
            val existed = cache.get(this) != null
            cache.evict(this)
            if (existed) 1 else 0
        }
    }

    override fun <T : Any> getOne(cfg: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        cache(cfg).get(tilNøkkel(cfg, id))?.get()?.let {
            clazz.java.cast(it)
        }

    override fun putOne(cfg: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) =
        cache(cfg).put(tilNøkkel(cfg, id), value)


    override fun <T : Any> getMany(cfg: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> {
        if (ids.isEmpty()) return emptyMap()
        val cache = cache(cfg)
        return ids.associateWith { id ->
            cache.get(tilNøkkel(cfg, id))?.get()?.let { clazz.java.cast(it) }
        }.filterValues { it != null }
    }

    override fun putMany(cfg: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        val cache = cache(cfg)
        log.trace("Caffeine bulk lagrer {} verdier for cache {}", innslag.size, cfg.name)
        innslag.forEach {
            (id, value) -> cache.put(tilNøkkel(cfg, id), value)
        }
    }

    override fun tilNøkkel(cfg: CacheNøkkelConfig, id: String): String {
        val extra = cfg.extraPrefix?.let { "$it:" } ?: ""
        return "$extra$id"
    }

    override fun clear(cfg: CacheNøkkelConfig) {
        val cache = cache(cfg)
        if (cfg.extraPrefix == null) {
            cache.clear()
        } else {
            nativeCache(cfg).asMap().keys
                .filterIsInstance<String>()
                .filter { it.startsWith(tilNøkkel(cfg, "")) }
                .forEach { cache.evict(it) }
        }
    }

    override fun size(cfg: CacheNøkkelConfig): Long {
        val cache = nativeCache(cfg)
        if (cfg.extraPrefix == null) {
            return cache.estimatedSize()
        }
        val prefix = tilNøkkel(cfg, "")
        return cache.asMap().keys.count { it is String && it.startsWith(prefix) }.toLong()
    }

    private fun cache(cfg: CacheNøkkelConfig) =
        requireNotNull(mgr.getCache(cfg.name)) {
            "Cache '${cfg.name}' ikke konfigurert i CacheManager"
        }


    private fun nativeCache(cfg: CacheNøkkelConfig) = cache(cfg).nativeCache as? Cache<*, *>
        ?: error("Forventet Caffeine Cache for '${cfg.name}'")
}
