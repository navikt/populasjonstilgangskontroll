package no.nav.tilgangsmaskin.felles.cache

import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations {
    fun delete(cfg: CacheNøkkelConfig, id: String): Long
    fun <T : Any> getOne(cfg: CacheNøkkelConfig, id: String, clazz: KClass<T>): T?
    fun putOne(cfg: CacheNøkkelConfig, id: String, value: Any, ttl: Duration)
    fun <T : Any> getMany(cfg: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?>
    fun putMany(cfg: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration)
    fun clear(cfg: CacheNøkkelConfig)
    fun clearAll(cfgs: Set<CacheNøkkelConfig>) = cfgs.forEach { clear(it) }
    fun size(cfg: CacheNøkkelConfig): Long
    fun sizes(cfgs: Set<CacheNøkkelConfig>): Map<String, Long> =
        cfgs.associate { it.fullName to size(it) }
}

inline fun <reified T : Any> CacheOperations.getOne(cfg: CacheNøkkelConfig, id: String): T? =
    getOne(cfg, id, T::class)

inline fun <reified T : Any> CacheOperations.getMany(cfg: CacheNøkkelConfig, ids: Set<String>): Map<String, T?> =
    getMany(cfg, ids, T::class)