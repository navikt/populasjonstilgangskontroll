package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.KeyValue
import io.lettuce.core.api.StatefulRedisConnection

class ValkeyCacheClient(val handler: ValkeyCacheKeyHandler,  val conn: StatefulRedisConnection<String,String>,  val mapper: ObjectMapper)  {


    inline fun <reified T> get(cache: String, id: String) =
        conn.sync().get(handler.toKey(cache,id))?.let { json ->
            mapper.readValue<T>(json)
        }

    inline fun <reified T> mget(cache: String, ids: Set<String>, extraPrefix: String? = null)  =
        if (ids.isEmpty()) {
            emptySet()
        }
        else conn.sync()
            .mget(*ids.map {id -> handler.toKey(cache,id)}.toTypedArray<String>())
            .filter { it.hasValue() }
            .map { handler.fromKey(cache,it.key,extraPrefix) to mapper.readValue<T>(it.value)
            }.toSet()

    fun put(cache: String, innslag: Map<String, Any>) =
        conn.sync().mset(innslag
            .mapKeys { handler.toKey(cache,it.key) }
            .mapValues { mapper.writeValueAsString(it.value) })
}