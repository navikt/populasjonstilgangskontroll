package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.data.redis.listener.support.PubSubHeaders.CHANNEL
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

private const val CHANNEL_EXPIRED = "__keyevent@0__:expired"
private const val CHANNEL_DELETED = "__keyevent@0__:del"

@Component
class ValkeyEventListeningCacheOppfrisker(erLeder: Boolean = true,
                                          private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder) {

    @RedisListener(CHANNEL_EXPIRED)
    @RedisListener(CHANNEL_DELETED)
    fun onEvent(nokkel: CacheNøkkel,@Header(CHANNEL) channel: String) {

        somLeder {
            oppfriskere.firstOrNull { it.cacheName == nokkel.cacheName }?.run {
                oppfrisk(nokkel)
            }
        }
    }

}
