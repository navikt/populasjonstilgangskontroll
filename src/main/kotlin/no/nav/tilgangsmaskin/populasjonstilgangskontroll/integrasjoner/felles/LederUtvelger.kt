package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient.Builder
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.SSEHandler.LeaderChangedEvent
@Service
class LederUtvelger :ApplicationListener<LeaderChangedEvent> {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder : Boolean = false

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}


@Component
class SSEHandler(private val builder: Builder, @Value("\${elector.sse.url}") private val uri: URI, val publisher: ApplicationEventPublisher) {
    init {
        builder.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(LederUtvelgerRespons::class.java).subscribe {
            publisher.publishEvent(LeaderChangedEvent(this,it.name))
        }
    }
    data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
    class LeaderChangedEvent(source: Any, val leder: String) : ApplicationEvent(source)
}
