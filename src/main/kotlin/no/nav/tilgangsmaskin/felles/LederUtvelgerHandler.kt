package no.nav.tilgangsmaskin.felles

import java.net.URI
import java.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient.Builder

@Component
class LederUtvelgerHandler(builder: Builder,
                           @Value("\${elector.sse.url}") uri: URI, publisher: ApplicationEventPublisher
) {
    init {
        builder.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(LederUtvelgerRespons::class.java).subscribe {
                publisher.publishEvent(LeaderChangedEvent(this, it.name))
            }
    }

    data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
    class LeaderChangedEvent(source: Any, val leder: String) : ApplicationEvent(source)
}