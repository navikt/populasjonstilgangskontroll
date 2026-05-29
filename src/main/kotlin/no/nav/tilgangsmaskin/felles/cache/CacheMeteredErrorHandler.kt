package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.cache.Cache
import org.springframework.stereotype.Component

@Component
class CacheMeteredErrorHandler(private val registry: MeterRegistry) : CacheErrorHandler {
    private val log = getLogger(javaClass)
    override fun handleCacheGetError(ex: RuntimeException, cache: Cache, key: Any) =
        record("get", cache, ex)

    override fun handleCachePutError(ex: RuntimeException, cache: Cache, key: Any, value: Any?) =
        record("put", cache, ex)

    override fun handleCacheEvictError(ex: RuntimeException, cache: Cache, key: Any) =
        record("evict", cache, ex)

    override fun handleCacheClearError(ex: RuntimeException, cache: Cache) =
        record("clear", cache, ex)

    private fun record(op: String, cache: Cache, ex: RuntimeException) {
        registry.counter("cache.operation.failed", "op", op, "cache", cache.name,
            "exception", ex.javaClass.simpleName).increment()
        log.warn("Cache $op feilet for ${cache.name}: ${ex.message}")
    }
}