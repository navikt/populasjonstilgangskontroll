package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8


@Component
class ValkeyListener {
    private val log = getLogger(javaClass)

    @RedisListener(CHANNEL)
    fun onExpired(body: ByteArray) {
        val key = body.toString(UTF_8)
        val cachePrefix = key.substringBefore("::", missingDelimiterValue = "")
        val rawKey = key.substringAfter("::", missingDelimiterValue = key)
        log.info("Valkey expired cachePrefix={}, key={}", cachePrefix, rawKey)
    }
    companion object {
        const val CHANNEL = "__keyevent@0__:expired"
    }
}