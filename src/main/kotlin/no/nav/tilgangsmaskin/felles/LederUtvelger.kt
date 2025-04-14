package no.nav.tilgangsmaskin.felles

import no.nav.tilgangsmaskin.felles.LederUtvelgerHandler.LeaderChangedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.net.InetAddress

@Service
class LederUtvelger : ApplicationListener<LeaderChangedEvent> {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}
