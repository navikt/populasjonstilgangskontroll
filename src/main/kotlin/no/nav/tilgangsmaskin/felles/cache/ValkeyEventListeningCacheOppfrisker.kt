package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.stereotype.Component

@Component
class ValkeyEventListeningCacheOppfrisker(erLeder: Boolean = true,
                                          private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder) {

    @RedisListener(CHANNEL)
    fun onEvent(nokkel: CacheNøkkel) {
        somLeder("Håndterer oppfrisking for ${nokkel.maskert}") {
            oppfriskere.firstOrNull { it.cacheName == nokkel.cacheName }?.run {
                oppfrisk(nokkel)
            }
        }
    }

    private companion object {
        private const val CHANNEL = "__keyevent@0__:expired"
    }
}

