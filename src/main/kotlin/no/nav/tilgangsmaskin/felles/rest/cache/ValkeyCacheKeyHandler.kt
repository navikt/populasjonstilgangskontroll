package no.nav.tilgangsmaskin.felles.rest.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration

class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: String, key: String, extraPrefix: String? = null) =
        ("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}$key").also {
            log.debug("La til prefix for cache $cache: $key -> $it")
        }

    fun fromKey(cache: String, key: String, extraPrefix: String? = null)  =
        key.removePrefix("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}").also {
            log.debug("Fjernet prefix for cache $cache: $key -> $it")
        }

    private fun prefixFor(cache: String): String =
        configs[cache]?.getKeyPrefixFor(cache)
            ?: throw IllegalStateException("Har ingen cache med navn $cache")

}