package no.nav.tilgangsmaskin.felles.cache

import no.nav.boot.conditionals.ConditionalOnLocalOrTest
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import kotlin.reflect.KClass

@Component
@ConditionalOnLocalOrTest
class LocalCacheOperations(
    private val cacheManager: CacheManager,
    private val objectMapper: JsonMapper,
) : CacheOperations {

    private fun cache(cfg: CachableConfig) =
        cacheManager.getCache(cfg.name) ?: error("Ingen cache med navn ${cfg.name}")

    override fun tilNøkkel(cache: CachableConfig, id: String): String =
        cache.extraPrefix?.let { "$it:$id" } ?: id

    override fun <T : Any> getOne(cache: CachableConfig, id: String, clazz: KClass<T>): T? =
        cache(cache).get(tilNøkkel(cache, id))?.get()?.let { deserialize(it, clazz) }

    override fun putOne(cache: CachableConfig, id: String, value: Any, ttl: Duration) {
        cache(cache).put(tilNøkkel(cache, id), objectMapper.writeValueAsString(value))
    }

    override fun delete(cache: CachableConfig, id: String): Long {
        val hit = cache(cache).get(tilNøkkel(cache, id)) != null
        cache(cache).evict(tilNøkkel(cache, id))
        return if (hit) 1L else 0L
    }

    override fun <T : Any> getMany(cache: CachableConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?> =
        ids.associateWith { getOne(cache, it, clazz) }

    override fun putMany(cache: CachableConfig, innslag: Map<String, Any>, ttl: Duration) =
        innslag.forEach { (id, value) -> putOne(cache, id, value, ttl) }

    private fun <T : Any> deserialize(value: Any, clazz: KClass<T>): T? =
        when (value) {
            is String -> objectMapper.readValue(value, clazz.java)
            else -> clazz.java.cast(value)
        }
}

