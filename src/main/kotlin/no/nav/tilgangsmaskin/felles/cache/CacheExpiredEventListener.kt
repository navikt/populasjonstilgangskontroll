package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import java.util.concurrent.atomic.AtomicBoolean
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheRemovalListener.CacheExpiredEvent
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.BulkCacheTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener( val teller: BulkCacheTeller,erLeder: Boolean = true,private vararg val oppfriskere: CacheOppfrisker) :LeaderAware(erLeder) {
    private val log = getLogger(javaClass)

    @EventListener
    fun cacheInnslagFjernet(hendelse: CacheExpiredEvent) {
        if (erLeder && !stopper.get()) {
            val elementer = CacheNøkkelElementer(hendelse.nøkkel)
            oppfriskere.firstOrNull { it.cacheName == elementer.cacheName }?.run {
                oppfrisk(elementer)
                teller.tell(of("cache", elementer.cacheName, "result", "expired", "method", elementer.metode ?: "ingen"))
            }
        }
        else    {
            log.info("Ignorerer cache expired event for ${hendelse.nøkkel} siden denne instansen ikke er leder")
        }
    }

    @EventListener
    fun stopper(event: ContextClosedEvent) {
        log.info("Applikasjonen holder på å stoppe")
        stopper.set(true)
    }
    companion object {
        private val stopper = AtomicBoolean(false)
    }
}