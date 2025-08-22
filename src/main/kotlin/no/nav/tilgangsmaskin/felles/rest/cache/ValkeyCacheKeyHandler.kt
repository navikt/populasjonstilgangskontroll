package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.data.redis.cache.RedisCacheConfiguration

class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {

    fun toKey(cache: String, key: String, extraPrefix: String? = null) =
        "${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}$key"

    fun fromKey(cache: String, key: String, extraPrefix: String? = null)  =
        key.removePrefix("${prefixFor(cache)}${extraPrefix?.let { "$it:" } ?: ""}")

    private fun prefixFor(cache: String): String =
        configs[cache]?.getKeyPrefixFor(cache)
            ?: throw IllegalStateException("Har ingen cache med navn $cache")

}