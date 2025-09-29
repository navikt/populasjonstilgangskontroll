package no.nav.tilgangsmaskin.felles.rest.cache

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component

@Component
class ValkeyCacheKeyMapper(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun toKey(cache: CacheConfig, key: String): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix::$extra$key".also {
            log.trace(CONFIDENTIAL, "Lagt til prefix for {} -> {}", key, it)
        }
    }

    fun fromKey(key: String): String {
        val deler = CacheNøkkelDeler(key)
        log.trace(CONFIDENTIAL,"Fjernet prefix for {} -> {}",key, deler.id)
        return deler.id
    }

    private fun prefixFor(cache: CacheConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: throw IllegalStateException("Ingen cache med navn ${cache.name}")

}


data class CacheConfig(val name: String, val extraPrefix: String? = null)