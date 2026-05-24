package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.KeyValue
import no.nav.tilgangsmaskin.felles.cache.CacheBeanConfig.Companion.VALKEY_MAPPER
import org.springframework.data.redis.cache.RedisCacheConfiguration
import tools.jackson.databind.json.JsonMapper
import kotlin.reflect.KClass

class CacheNøkkelMapper(private val configs: Map<String, RedisCacheConfiguration?>, private val mapper: JsonMapper = VALKEY_MAPPER) {

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

    fun tilJsonEntry(cache: CacheNøkkelConfig, key: String, value: Any) =
        tilNøkkel(cache, key) to tilJson(value)

     fun <T : Any> tilEntry(it: KeyValue<String, String>, clazz: KClass<T>) =
        idFraNøkkel(it.key) to fraJson(it.value, clazz)
}



