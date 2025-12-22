package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.rest.Pingable
import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations : Pingable {

    @WithSpan
    fun delete( id: String,vararg caches: CachableConfig): Long

    @WithSpan
    fun <T : Any> getOne(id: String,clazz: KClass<T>,cache: CachableConfig): T?

    @WithSpan
    fun putOne( id: String, value: Any, ttl: Duration,cache: CachableConfig)

    @WithSpan
    fun <T : Any> getMany( ids: Set<String>, clazz: KClass<T>,cache: CachableConfig): Map<String, T>

    @WithSpan
    fun putMany(innslag: Map<String, Any>, ttl: Duration,cache: CachableConfig)
}
