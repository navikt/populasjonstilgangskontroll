package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations  {

    @WithSpan
    fun delete( id: String, cache: CachableConfig): Long

    @WithSpan
    fun <T : Any> getOne(id: String,clazz: KClass<T>,cache: CachableConfig): T?

    @WithSpan
    fun putOne(id: String, value: Any, ttl: Duration, cache: CachableConfig)

    @WithSpan
    fun <T : Any> getMany(ids: Set<String>, clazz: KClass<T>, cache: CachableConfig): Map<String, T>

    @WithSpan
    fun putMany(innslag: Map<String, Any>, ttl: Duration,cache: CachableConfig)

    fun tilNøkkel(cache: CachableConfig, id: String): String

}