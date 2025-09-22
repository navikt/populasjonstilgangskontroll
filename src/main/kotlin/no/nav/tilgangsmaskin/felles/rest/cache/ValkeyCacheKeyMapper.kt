package no.nav.tilgangsmaskin.felles.rest.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component

@Component
class ValkeyCacheKeyMapper(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: CacheConfig, key: String): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix::$extra$key"
    }

    fun fromKey(key: String): String {
        val (cache, _, id) = detaljerFra(key)
        log.trace("Fjernet prefix for  {}: {} -> {}", cache, key, id)
        return id
    }

    private fun prefixFor(cache: CacheConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: throw IllegalStateException("Har ingen cache med navn ${cache.name}")

    fun detaljerFra(key: String) =
        with(key.split("::", ":")) {
            Triple(first(), if (size > 2) this[1] else null,last() )
        }
}

 data class CacheConfig(val name: String, val extraPrefix: String? = null)