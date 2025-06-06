package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.nom.LederUtvelgerHandler.LeaderChangedEvent
import java.net.URI
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient.Builder
import java.net.InetAddress

@Component
class LederUtvelgerHandler(private val builder: Builder,
                           @Value("\${elector.sse.url}") private val uri: URI,
                           private val publisher: ApplicationEventPublisher) {


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        builder.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(LederUtvelgerRespons::class.java)
            .subscribe {
                publisher.publishEvent(LeaderChangedEvent(this, it.name))
            }
    }

    private data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
    class LeaderChangedEvent(source: Any, val leder: String) : ApplicationEvent(source)
}

@Component
class LederUtvelger  {

    private val hostname = InetAddress.getLocalHost().hostName
    var erLeder: Boolean = false

    @EventListener(LeaderChangedEvent::class)
    fun onApplicationEvent(event: LeaderChangedEvent) {
        erLeder = event.leder == hostname
    }
}