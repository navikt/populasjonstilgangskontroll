package no.nav.tilgangsmaskin.felles.cache

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component

@Component
class CacheNøkkelMapper(val configs: Map<String, RedisCacheConfiguration>) {
    private val log = getLogger(javaClass)

    fun tilNøkkel(cache: CachableConfig, nøkkel: String): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix::$extra$nøkkel".also {
            log.trace(CONFIDENTIAL, "Lagt til prefix for {} -> {}", nøkkel, it)
        }
    }

    fun fraNøkkel(nøkkel: String): String {
        val elementer = CacheNøkkelElementer(nøkkel)
        log.trace(CONFIDENTIAL,"Fjernet prefix for {} -> {}",nøkkel, elementer.id)
        return elementer.id
    }

    private fun prefixFor(cache: CachableConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: throw IllegalStateException("Ingen cache med navn ${cache.name}")

    data class CacheNøkkelElementer(val nøkkel: String) {
        private val elementer = nøkkel.split("::", ":")
        val cacheName = elementer.first()
        val metode = if (elementer.size > 2) elementer[1] else null
        val id = elementer.last()
    }
}

data class CachableConfig(val name: String, val extraPrefix: String? = null)

