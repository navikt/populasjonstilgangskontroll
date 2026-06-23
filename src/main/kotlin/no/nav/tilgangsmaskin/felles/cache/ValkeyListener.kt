package no.nav.tilgangsmaskin.felles.cache

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.data.redis.listener.Topic
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8


@Component
class ValkeyListener {
    private val log = getLogger(javaClass)

    @RedisListener(CHANNEL)
    fun onEvent(body: ByteArray, topic: Topic) {
        val theBody = body.toString(UTF_8)
        val nøkkel = CacheNøkkel(theBody)
        log.info("Valkey expired event {} på topic {}", nøkkel,topic.topic)
    }
    companion object {
        const val CHANNEL = "__keyevent@0__:expired"
    }
}