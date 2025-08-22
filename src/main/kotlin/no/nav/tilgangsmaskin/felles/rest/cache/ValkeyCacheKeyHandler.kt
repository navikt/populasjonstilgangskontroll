package no.nav.tilgangsmaskin.felles.rest.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration

class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: String, key: String, extraPrefix: String? = null) =
        ("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}$key").also {
            log.info("La til prefix (med extra $extraPrefix) for cache $cache: $key -> $it")
        }

    fun fromKey(cache: String, key: String, extraPrefix: String? = null)  =
        key.removePrefix("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}").also {
            log.info("Fjernet prefix (med extra $extraPrefix) for cache $cache: $key -> $it")
        }

    private fun prefixFor(cache: String): String =
        configs[cache]?.getKeyPrefixFor(cache)
            ?: throw IllegalStateException("Har ingen cache med navn $cache")

}