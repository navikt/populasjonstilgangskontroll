package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import org.springframework.data.redis.cache.RedisCacheConfiguration
import tools.jackson.databind.json.JsonMapper
import kotlin.reflect.KClass

class CacheNøkkelMapper(val configs: Map<String, RedisCacheConfiguration?>, val mapper: JsonMapper = VALKEY_MAPPER) {


    fun <T : Any> fraJson(json: String, clazz: KClass<T>): T =
        mapper.readValue(json, clazz.java)

    fun tilJson(value: Any): String =
        mapper.writeValueAsString(value)

    fun tilNøkkel(cache: CacheNøkkelConfig, nøkkel: String): String {
        val prefix = prefixFor(cache)
        val extra = cache.extraPrefix?.let { "$it:" } ?: ""
        return "$prefix$extra$nøkkel"
    }

    fun idFraNøkkel(nøkkel: String) =
        CacheNøkkel(nøkkel).id

    private fun prefixFor(cache: CacheNøkkelConfig): String =
        configs[cache.name]?.getKeyPrefixFor(cache.name)
            ?: error("Ingen cache med navn ${cache.name}")

}

data class CacheNøkkelConfig(val name: String, val extraPrefix: String? = null)
