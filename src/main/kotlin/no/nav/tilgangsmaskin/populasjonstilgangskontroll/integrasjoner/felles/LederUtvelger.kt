package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LederUtvelgerHandler.LeaderChangedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import java.net.InetAddress

@Service
class LederUtvelger :ApplicationListener<LeaderChangedEvent> {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder : Boolean = false

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}
