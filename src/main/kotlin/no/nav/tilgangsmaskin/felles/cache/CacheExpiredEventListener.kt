package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener( val teller: BulkCacheTeller,erLeder: Boolean = true,private vararg val oppfriskere: CacheOppfrisker) :LeaderAware(erLeder){
    @EventListener
    fun handleCacheExpired(event: CacheExpiredEvent) {
        if (erLeder) {
            val deler = CacheNøkkelDeler(event.nøkkel)
            oppfriskere.firstOrNull { it.cacheName == deler.cacheName }?.oppfrisk(deler).also {
                teller.tell(of("cache", deler.cacheName, "result", "expired", "method", deler.metode ?: "ingen"))
            }
        }
    }
}