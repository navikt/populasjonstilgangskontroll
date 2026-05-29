package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException

class ResilientRedisSerializer(private val delegate: RedisSerializer<Any>, private val meterRegistry: MeterRegistry) : RedisSerializer<Any> {

    private val log = getLogger(javaClass)

    override fun serialize(t: Any?): ByteArray = delegate.serialize(t)

    override fun deserialize(bytes: ByteArray?): Any? =
        try {
            delegate.deserialize(bytes)
        } catch (e: SerializationException) {
            meterRegistry.counter("cache.deserialize.failed").increment()
            log.warn("Kunne ikke deserialisere cache-entry, behandler som miss: ${e.message}")
            null
        }
}