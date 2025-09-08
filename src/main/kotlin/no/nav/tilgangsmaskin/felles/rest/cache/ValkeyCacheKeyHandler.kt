package no.nav.tilgangsmaskin.felles.rest.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component

@Component
class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: CacheConfig, key: String) =
        ("${prefixFor(cache)}${cache.extraPrefix?.let { "$it:" } ?: ""}$key").also {
            log.trace("La til prefix for cache {}: {} -> {}", cache, key, it)
        }

    fun fromKey(cache: CacheConfig, key: String)  =
        key.removePrefix("${prefixFor(cache)}${cache.extraPrefix?.let { "$it:" } ?: ""}").also {
            log.trace("Fjernet prefix for cache {}: {} -> {}",  cache, key, it)
        }

    private fun prefixFor(cache: CacheConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: throw IllegalStateException("Har ingen cache med navn ${cache.name}")
}

 data class CacheConfig(val name: String, val extraPrefix: String? = null)