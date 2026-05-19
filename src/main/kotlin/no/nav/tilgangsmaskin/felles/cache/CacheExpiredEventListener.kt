package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.Tags.of
import no.nav.tilgangsmaskin.felles.cache.CacheElementUtløptLytter.CacheInnslagFjernetHendelse
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.regler.motor.CacheOppfriskerTeller
import org.springframework.context.SmartLifecycle
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CacheExpiredEventListener(val teller: CacheOppfriskerTeller,
                                erLeder: Boolean = true,
                                private vararg val oppfriskere: CacheOppfrisker) : LeaderAware(erLeder),
    SmartLifecycle {
    private var running = false

    @EventListener
    fun cacheInnslagFjernet(hendelse: CacheInnslagFjernetHendelse) {
        val nøkkel = CacheNøkkel(hendelse.nøkkel)
        somLeder("håndterer fjernet cache innslag ${nøkkel.maskert}", {
            if (isRunning()) {
                with(nøkkel) {
                    oppfriskere.firstOrNull { it.cacheName == cacheName }?.run {
                        oppfrisk(nøkkel)
                        teller.tell(of("cache", cacheName, "result", "expired", "method", metode ?: "ingen"))
                    }
                }
            }
        }) {}
    }

    override fun start() {
        running = true
    }

    override fun isRunning() = running
    override fun stop() {
        running = false
    }
}