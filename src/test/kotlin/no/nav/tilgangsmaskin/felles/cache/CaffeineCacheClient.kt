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

    override fun delete(cfg: CacheNøkkelConfig, id: String) =
        with(cache(cfg)) {
            val nøkkel = cfg.tilNøkkel(id)
            if (get(nøkkel) != null) {
                evict(nøkkel)
                1L
            } else {
                0L
            }
        }

    override fun <T : Any> getOne(cfg: CacheNøkkelConfig, id: String, clazz: KClass<T>): T? =
        cache(cfg).get(cfg.tilNøkkel(id))?.get()?.let {
            clazz.java.cast(it)
        }

    override fun putOne(cfg: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) =
        cache(cfg).put(cfg.tilNøkkel(id), value)


    override fun <T : Any> getMany(cfg: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> {
        val keys = ids.associateBy { cfg.tilNøkkel(it) }
        val found = typedCache(cfg).getAllPresent(keys.keys)
        return found.entries.associate { (key, value) -> keys.getValue(key) to clazz.java.cast(value) }
    }

    override fun putMany(cfg: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) {
        log.trace("Caffeine bulk lagrer {} verdier for cache {}", innslag.size, cfg.name)
        val keyed = innslag.map { (id, value) -> cfg.tilNøkkel(id) to value }.toMap()
        typedCache(cfg).putAll(keyed)
    }

    override fun clear(cfg: CacheNøkkelConfig) {
        val cache = cache(cfg)
        if (cfg.extraPrefix == null) {
            cache.clear()
        } else {
            nativeCache(cfg).asMap().keys
                .filterIsInstance<String>()
                .filter { it.startsWith(cfg.tilNøkkel( "")) }
                .forEach { cache.evict(it) }
        }
    }

    override fun size(cfg: CacheNøkkelConfig): Long {
        val cache = nativeCache(cfg)
        if (cfg.extraPrefix == null) {
            return cache.estimatedSize()
        }
        val prefix = cfg.tilNøkkel( "")
        return cache.asMap().keys.count { it is String && it.startsWith(prefix) }.toLong()
    }

    private fun cache(cfg: CacheNøkkelConfig) =
        requireNotNull(mgr.getCache(cfg.name)) {
            "Cache '${cfg.name}' ikke konfigurert i CacheManager"
        }

    private fun nativeCache(cfg: CacheNøkkelConfig) =
        cache(cfg).nativeCache as? Cache<*, *>
            ?: error("Forventet Caffeine Cache for '${cfg.name}'")

    @Suppress("UNCHECKED_CAST")
    private fun typedCache(cfg: CacheNøkkelConfig): Cache<String, Any> =
        nativeCache(cfg) as Cache<String, Any>
}
