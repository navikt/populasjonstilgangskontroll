package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheRemovalListener.CacheExpiredEvent
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener( val teller: BulkCacheTeller,erLeder: Boolean = true,private vararg val oppfriskere: CacheOppfrisker) :LeaderAware(erLeder){
    private val log = getLogger(javaClass)
    @EventListener
    fun cacheInnslagFjernet(hendelse: CacheExpiredEvent) {
        log.info("Cache-innslag utløpt: ${hendelse.nøkkel} - erLeder $erLeder")
        if (erLeder) {
            with(CacheNøkkelElementer(hendelse.nøkkel)) {
                oppfriskere.firstOrNull { it.cacheName == cacheName }?.oppfrisk(this).also {
                    teller.tell(of("cache", cacheName, "result", "expired", "method", metode ?: "ingen"))
                }
            }
        }
    }
}