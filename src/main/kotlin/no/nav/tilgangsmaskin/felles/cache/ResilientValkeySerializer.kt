package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.serializer.RedisSerializer

class ResilientValkeySerializer(private val delegate: RedisSerializer<Any>
) : RedisSerializer<Any> {

    private val log = getLogger(javaClass)

    override fun serialize(t: Any?): ByteArray = delegate.serialize(t)

    override fun deserialize(bytes: ByteArray?): Any? =
        runCatching {
            delegate.deserialize(bytes)
        }.getOrElse {
            log.warn("Kunne ikke deserialisere cache-entry, behandler som miss", it)
            null
        }
}