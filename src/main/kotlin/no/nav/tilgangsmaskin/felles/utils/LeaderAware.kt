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
            log.info("Denne instansen ($hostname) ER nå leder")
            doHandleLeaderChange()
        }) { log.info("Denne instansen ($hostname) er IKKE leder, lederen er ${event.leder}") }
    }
    protected fun <T> somLeder(beskrivelse: String, block: () -> T, default: () -> T): T = if (erLeder) {
        log.trace("Kjører $beskrivelse som leder")
        block()
    } else {
        log.trace("Kjører ikke $beskrivelse som leder, returnerer default")
        default()
    }
}