package no.nav.tilgangsmaskin.ansatt.nom

import java.net.InetAddress
import no.nav.tilgangsmaskin.ansatt.nom.LederUtvelgerHandler.LeaderChangedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service


@Service
class LederUtvelger  {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    @EventListener(LeaderChangedEvent::class)
    fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}
