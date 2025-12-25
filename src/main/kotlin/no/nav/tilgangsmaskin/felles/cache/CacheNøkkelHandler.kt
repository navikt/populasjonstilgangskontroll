package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.cache.RedisCacheConfiguration
import tools.jackson.databind.json.JsonMapper
import kotlin.reflect.KClass

class CacheNøkkelHandler(val configs: Map<String, RedisCacheConfiguration?>, val mapper: JsonMapper) {

    private val log = getLogger(javaClass)

     fun <T : Any> json(json: String, clazz: KClass<T>): T =
        mapper.readValue(json, clazz.java)

    fun json(value: Any) =
        mapper.writeValueAsString(value)

    fun `nøkkel`(`nøkkel`: String, cache: CachableConfig): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix$extra$nøkkel"
    }

    fun id(nøkkel: String) = CacheNøkkelElementer(nøkkel).id

    private fun prefixFor(cache: CachableConfig)  =
        configs[cache.name]?.getKeyPrefixFor(cache.name).also {
            log.debug("Prefix for cache ${cache.name} er: $it")
        } ?: error("Ingen cache med navn ${cache.name}")
}

data class CachableConfig(val name: String, val extraPrefix: String? = null)

