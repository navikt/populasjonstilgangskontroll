package no.nav.tilgangsmaskin.felles.utils

import no.nav.tilgangsmaskin.felles.utils.LederUtvelger.LeaderChangedEvent
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import java.net.InetAddress

abstract class LeaderAware(var erLeder: Boolean = false) {
    private val hostname = InetAddress.getLocalHost().hostName
    protected open fun doHandleLeaderChange()  = Unit

    private val log = getLogger(javaClass)

    @EventListener(LeaderChangedEvent::class)
    fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
        if (erLeder) {
            log.info("Denne instansen ($hostname) er nå leder")
            doHandleLeaderChange()
        }
        else {
            log.info("Denne instansen ($hostname) er IKKE leder, lederen er ${event.leder}")
        }
    }
}