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
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LeaderChangedEventPublisher.LeaderChangedEvent




@Service
class LederUtvelger(private val webClient: Builder) :ApplicationListener<LeaderChangedEvent> {

    var erLeder : Boolean = false

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leader == InetAddress.getLocalHost().hostName
    }

    fun start(uri: URI) =
         webClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(LederUtvelgerRespons::class.java)

    data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)

}


@Component
private class SseSubscriber(private val utvelger: LederUtvelger, @Value("\${elector.sse.url}") private val uri: URI, val publisher: LeaderChangedEventPublisher) {
    init {
        start()
    }
    private final fun start() {
        utvelger.start(uri).subscribe {
            publisher.publish(it.name)
        }
    }
}

@Component
class LeaderChangedEventPublisher(private val publisher: ApplicationEventPublisher) {

    fun publish(leader: String) {
        publisher.publishEvent(LeaderChangedEvent(this,leader))
    }

    class LeaderChangedEvent(source: Any, val leader: String) : ApplicationEvent(source)
}

