package no.nav.tilgangsmaskin.felles.cache

import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations {
    fun delete(cache: CacheNøkkelConfig, id: String): Long
    fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T?
    fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration)
    fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?>
    fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration)
    fun tilNøkkel(cache: CacheNøkkelConfig, id: String): String
}