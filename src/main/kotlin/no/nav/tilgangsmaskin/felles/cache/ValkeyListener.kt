package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.data.redis.listener.support.PubSubHeaders.CHANNEL
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class ValkeyListener {
    private val log = getLogger(javaClass)

    @RedisListener(THECHANNEL)
    fun onEvent(body: ByteArray, @Header(CHANNEL) channel: String) {
        val nøkkel = CacheNøkkel(body.toString(UTF_8))
        log.info("Valkey expired event {} på channel {}", nøkkel, channel)
    }

    companion object {
        private const val THECHANNEL = "__keyevent@0__:expired"
    }
}