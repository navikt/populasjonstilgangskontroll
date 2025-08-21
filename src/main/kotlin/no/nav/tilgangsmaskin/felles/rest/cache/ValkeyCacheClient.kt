package no.nav.tilgangsmaskin.felles.rest.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.KeyValue
import io.lettuce.core.RedisClient
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component

@Component
class ValkeyCacheClient(val handler: ValkeyCacheKeyHandler, cfg: ValKeyConfig, private val cf: RedisConnectionFactory) {

    val conn = RedisClient.create(cfg.valkeyURI).connect()
    val mapper = jacksonObjectMapper().apply {
        activateDefaultTyping(polymorphicTypeValidator, EVERYTHING,PROPERTY)
    }

    inline fun <reified T> get(cache: String, id: String) =
        conn.sync().get(handler.toKey(cache,id))?.let { json ->
            mapper.readValue<T>(json)
        }

    inline fun <reified T> mget(cache: String, ids: Set<String>, extraPrefix: String? = null)  =
        if (ids.isEmpty()) { emptySet() }
        else conn.sync()
            .mget(*ids.map {key -> handler.toKey(cache,key) }.toTypedArray<String>())
            .filter { it.hasValue() }
            .map<KeyValue<String, String>, Pair<String, T>> { handler.fromKey(cache,it.key,extraPrefix) to mapper.readValue<T>(it.value)
            }.toSet()

    fun put(cache: String, innslag: Map<String, Any>): Int {
        if (innslag.isEmpty()) {return 0}
        conn.sync().mset(innslag
            .mapKeys { handler.toKey(it.key,cache) }
            .mapValues { mapper.writeValueAsString(it.value) }
        )
        return innslag.size
    }
}