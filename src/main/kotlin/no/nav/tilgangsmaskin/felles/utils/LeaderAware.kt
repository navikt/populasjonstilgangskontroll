package no.nav.tilgangsmaskin.felles.utils

import java.net.InetAddress
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener

abstract class LeaderAware(var erLeder: Boolean = false) {
    private val hostname = InetAddress.getLocalHost().hostName
    protected fun doHandleLeaderChange()  = Unit

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(LederUtvelger.LeaderChangedEvent::class)
    fun onApplicationEvent(event: LederUtvelger.LeaderChangedEvent) {
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