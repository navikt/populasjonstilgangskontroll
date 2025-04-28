package no.nav.tilgangsmaskin.ansatt.nom

import java.net.InetAddress
import no.nav.tilgangsmaskin.ansatt.nom.LederUtvelgerHandler.LeaderChangedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service


@Service
class LederUtvelger : ApplicationListener<LeaderChangedEvent> {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}
