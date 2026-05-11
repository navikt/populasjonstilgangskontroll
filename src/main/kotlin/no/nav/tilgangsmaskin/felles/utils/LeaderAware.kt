package no.nav.tilgangsmaskin.felles.utils

import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import java.net.InetAddress

abstract class LeaderAware(private var erLeder: Boolean = false) {
    private val hostname = InetAddress.getLocalHost().hostName
    protected open fun doHandleLeaderChange()  = Unit

    private val log = getLogger(javaClass)

    @EventListener(LeaderChangedEvent::class)
    fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
        somLeder("håndtering av lederbytte", {
            log.info("Denne instansen ($hostname) er nå leder")
            doHandleLeaderChange()
        }) { log.info("Denne instansen ($hostname) er ikke leder, lederen er ${event.leder}") }
    }
    protected fun <T> somLeder(beskrivelse: String, block: () -> T, default: () -> T): T = if (erLeder) {
        log.trace("Leder kjører $beskrivelse")
        block()
    } else {
        default()
    }
}