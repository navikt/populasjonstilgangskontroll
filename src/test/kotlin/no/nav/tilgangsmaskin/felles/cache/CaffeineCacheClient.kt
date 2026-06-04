package no.nav.tilgangsmaskin.felles.cache

import com.github.benmanes.caffeine.cache.Cache
import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import org.springframework.cache.CacheManager
import java.time.Duration
import kotlin.reflect.KClass

@ConditionalOnLocalOrTest
class CaffeineCacheClient(private val mgr: CacheManager) : CacheOperations {


    override fun delete(cfg: CacheNøkkelConfig, id: String) =
        if (cache(cfg).evictIfPresent(cfg.tilNøkkel(id))) 1L else 0L

    override fun <T : Any> getOne(cfg: CacheNøkkelConfig, id: String, clazz: KClass<T>) =
        cache(cfg).get(cfg.tilNøkkel(id))?.get()?.let {
            clazz.java.cast(it)
        }

    override fun putOne(cfg: CacheNøkkelConfig, id: String, value: Any, ttl: Duration) =
        cache(cfg).put(cfg.tilNøkkel(id), value)

    override fun <T : Any> getMany(cfg: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>) =
        with(ids.associateBy { cfg.tilNøkkel(it) }) {
            typedCache(cfg).getAllPresent(keys).entries.associate { (key, value) ->
                getValue(key) to clazz.java.cast(value)
            }
        }

    override fun putMany(cfg: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration) =
        typedCache(cfg).putAll(innslag.map {
            (id, value) -> cfg.tilNøkkel(id) to value
        }.toMap())

    override fun clear(cfg: CacheNøkkelConfig) {
        if (cfg.extraPrefix == null) {
            cache(cfg).clear()
        } else {
            with(typedCache(cfg))  {
                val prefix = cfg.tilNøkkel("")
                invalidateAll(asMap().keys.filter {
                    it.startsWith(prefix)
                })
            }
        }
    }

    override fun size(cfg: CacheNøkkelConfig) =
        with(typedCache(cfg)) {
            if (cfg.extraPrefix == null) {
                estimatedSize()
            } else {
                val prefix = cfg.tilNøkkel("")
                asMap().keys.count {
                    it.startsWith(prefix)
                }.toLong()
            }
        }

    private fun cache(cfg: CacheNøkkelConfig) =
        requireNotNull(mgr.getCache(cfg.name)) {
            "Cache '${cfg.name}' ikke konfigurert i CacheManager" // never
        }

    private fun nativeCache(cfg: CacheNøkkelConfig) =
        cache(cfg).nativeCache as? Cache<*, *>
            ?: error("Forventet Caffeine Cache for '${cfg.name}'") // never

    @Suppress("UNCHECKED_CAST")
    private fun typedCache(cfg: CacheNøkkelConfig): Cache<String, Any> =
        nativeCache(cfg) as Cache<String, Any>
}
