package no.nav.tilgangsmaskin.felles.rest.cache

import org.hibernate.annotations.Comment
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component

@Component
class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: CacheName, key: String, extraPrefix: String? = null) =
        ("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}$key").also {
            log.trace("La til prefix (med extra $extraPrefix) for cache $cache: $key -> $it")
        }

    fun fromKey(cache: CacheName, key: String, extraPrefix: String? = null)  =
        key.removePrefix("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}").also {
            log.trace("Fjernet prefix (med extra $extraPrefix) for cache $cache: $key -> $it")
        }

    private fun prefixFor(cache: CacheName): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: throw IllegalStateException("Har ingen cache med navn ${cache.name}")
}

@JvmInline
value class CacheName(val name: String)