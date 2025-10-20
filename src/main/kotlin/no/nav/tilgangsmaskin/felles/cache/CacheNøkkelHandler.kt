package no.nav.tilgangsmaskin.felles.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration

class CacheNøkkelHandler(val configs: Map<String, RedisCacheConfiguration>, val mapper: ObjectMapper) {
    private val log = getLogger(javaClass)

    inline fun <reified T> fraJson(json: String): T =
        mapper.readValue(json)

    fun tilJson(value: Any): String =
        mapper.writeValueAsString(value)

    fun tilNøkkel(cache: CachableConfig, nøkkel: String): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix::$extra$nøkkel"
    }

    fun idFraNøkkel(nøkkel: String) = CacheNøkkelElementer(nøkkel).id

    private fun prefixFor(cache: CachableConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: error("Ingen cache med navn ${cache.name}")

}

data class CachableConfig(val name: String, val extraPrefix: String? = null)

