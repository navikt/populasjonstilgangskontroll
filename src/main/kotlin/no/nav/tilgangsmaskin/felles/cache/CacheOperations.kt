package no.nav.tilgangsmaskin.felles.cache

import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations {
    fun delete(cache: CachableConfig, id: String): Long
    fun <T : Any> getOne(id: String, cache: CachableConfig, clazz: KClass<T>): T?
    fun putOne(id: String, cache: CachableConfig, value: Any, ttl: Duration)
    fun <T : Any> getMany(ids: Set<String>, cache: CachableConfig, clazz: KClass<T>): Map<String, T?>
    fun putMany(innslag: Map<String, Any>, cache: CachableConfig, ttl: Duration)
    fun tilNøkkel(cache: CachableConfig, id: String): String
}