package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetEvent
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.CacheOppfriskerTeller
import org.springframework.context.SmartLifecycle
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener(val teller: CacheOppfriskerTeller, erLeder: Boolean = true, private vararg val oppfriskere: CacheOppfrisker) :LeaderAware(erLeder), SmartLifecycle {
    private var running = false
    @EventListener
    fun cacheInnslagFjernet(hendelse: CacheInnslagFjernetEvent) {
        somLeder("håndtering av cache innslag ${hendelse.nøkkel} fjernet", {
            if (isRunning()) {
                val nøkkel = CacheNøkkel(hendelse.nøkkel)
                oppfriskere.firstOrNull { it.cacheName == nøkkel.cacheName }?.run {
                    oppfrisk(nøkkel)
                    teller.tell(of("cache", nøkkel.cacheName, "result", "expired", "method", nøkkel.metode ?: "ingen"))
                }
            }
        }) {}
    }

    override fun start() { running = true }
    override fun isRunning() = running
    override fun stop() { running = false }
}