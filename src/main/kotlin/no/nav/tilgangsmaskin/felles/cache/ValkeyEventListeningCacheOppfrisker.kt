package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.springframework.data.redis.annotation.RedisListener
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class ValkeyEventListeningCacheOppfrisker(erLeder: Boolean = true,
                                          private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder) {

    @RedisListener(CHANNEL)
    fun onEvent(body: ByteArray) {
        val nøkkel = body.tilNøkkel()
        somLeder("Håndterer utløpt cache-innslag for $nøkkel", {
                oppfriskere.firstOrNull {
                    it.cacheName == nøkkel.cacheName
                }?.run {
                    oppfrisk(nøkkel)
                }
        })
    }

    private companion object {
         private const val CHANNEL = "__keyevent@0__:expired"
    }

    private fun ByteArray.tilNøkkel() = CacheNøkkel(toString(UTF_8))

}

