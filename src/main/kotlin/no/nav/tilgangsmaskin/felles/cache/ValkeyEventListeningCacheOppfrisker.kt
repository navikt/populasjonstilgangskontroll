package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import org.springframework.stereotype.Component

@Component
class ValkeyEventListeningCacheOppfrisker(erLeder: Boolean = true,
                                          private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder) {

    fun onEvent(nokkel: CacheNøkkel) {
        somLeder {
            oppfriskere.firstOrNull { it.cacheName == nokkel.cacheName }?.run {
                oppfrisk(nokkel)
            }
        }
    }

}
