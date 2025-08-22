package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.data.redis.cache.RedisCacheConfiguration

class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {

    fun toKey(cache: String, key: String, extraPrefix: String? = null): String =
        if (extraPrefix != null) "${prefixFor(cache)}$extraPrefix:$key"
        else "${prefixFor(cache)}$key"

    fun fromKey(cache: String, key: String, extraPrefix: String? = null): String =
        if (extraPrefix != null) key.removePrefix(prefixFor(cache) + extraPrefix + ":")
        else key.removePrefix(prefixFor(cache))

    private fun prefixFor(cache: String): String =
        configs[cache]?.getKeyPrefixFor(cache)
            ?: throw IllegalStateException("Har ingen cache med navn $cache")

}