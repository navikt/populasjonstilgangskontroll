package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.ValkeyListenerConfig.Companion.CHANNEL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.stereotype.Component

@Configuration
class ValkeyListenerConfig {
    companion object {
        const val CHANNEL = "__keyevent@0__:expired"
    }
}

@Component
class ValkeyListener {
    private val log = getLogger(javaClass)

    @RedisListener(CHANNEL)
    fun onExpired(body: ByteArray) {
        val key = body.toString(Charsets.UTF_8)
        log.info("Valkey expired key={}", key)
    }
}