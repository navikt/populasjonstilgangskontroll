package no.nav.tilgangsmaskin.felles.cache

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.felles.rest.Pingable
import java.time.Duration
import kotlin.reflect.KClass

interface CacheOperations : Pingable {

    @WithSpan
    fun delete( id: String, cache: CachableConfig): Boolean

    @WithSpan
    fun <T : Any> get(id: String, clazz: KClass<T>, cache: CachableConfig): T?

    @WithSpan
    fun put(id: String, verdi: Any, ttl: Duration, cache: CachableConfig) : Boolean

    @WithSpan
    fun <T : Any> get(ids: Set<String>, clazz: KClass<T>, cache: CachableConfig): Map<String, T>

    @WithSpan
    fun put(verdier: Map<String, Any>, ttl: Duration, cache: CachableConfig)
}
