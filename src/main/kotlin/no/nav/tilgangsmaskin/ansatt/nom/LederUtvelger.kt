package no.nav.tilgangsmaskin.ansatt.nom

import java.net.InetAddress
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service

@Service
class LederUtvelger : ApplicationListener<LederUtvelgerHandler.LeaderChangedEvent> {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    override fun onApplicationEvent(event: LederUtvelgerHandler.LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}
