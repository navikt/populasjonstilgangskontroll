package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener( val teller: BulkCacheTeller,erLeder: Boolean = true,private vararg val oppfriskere: CacheOppfrisker) :LeaderAware(erLeder){
    @EventListener
    fun cacheInnslagFjernet(hendelse: CacheExpiredEvent) {
        if (erLeder) {
            with(CacheNøkkelElementer(hendelse.nøkkel)) {
                oppfriskere.firstOrNull { it.cacheName == cacheName }?.oppfrisk(this).also {
                    teller.tell(of("cache", cacheName, "result", "expired", "method", metode ?: "ingen"))
                }
            }
        }
    }
}