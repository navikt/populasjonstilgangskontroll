package no.nav.tilgangsmaskin.felles.rest.cache

import org.springframework.data.redis.cache.RedisCacheConfiguration

class ValkeyCacheKeyHandler(val configs: Map<String, RedisCacheConfiguration>) {

    fun toKey(key: String, cache: String, extraPrefix: String? = null): String =
        if (extraPrefix != null) "${prefixFor(cache)}$extraPrefix:$key"
        else "${prefixFor(cache)}$key"

    fun fromKey(key: String, cache: String, extraPrefix: String? = null): String =
        if (extraPrefix != null) key.removePrefix(prefixFor(cache) + extraPrefix + ":")
        else key.removePrefix(prefixFor(cache))

    private fun prefixFor(cache: String): String =
        configs[cache]?.getKeyPrefixFor(cache)
            ?: throw IllegalStateException("Har ingen cache med navn $cache")

    private fun Map<String, RedisCacheConfiguration>.prefixFor(cache: String) = get(cache)?.getKeyPrefixFor(cache) ?: throw IllegalStateException("Har ingen cache med navn $cache")
}