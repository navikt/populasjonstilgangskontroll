package no.nav.tilgangsmaskin.felles.utils

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient.Builder
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.Disposable
import java.net.URI
import java.time.LocalDateTime
import kotlin.text.get

@Component
class LederUtvelger(private val builder: Builder,
                    @param:Value("\${elector.sse.url}") private val uri: URI,
                    private val publisher: ApplicationEventPublisher) {

    protected val log = getLogger(javaClass)
    private lateinit var subscription: Disposable

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        subscription = builder.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux<LederUtvelgerRespons>()
            .subscribe(
                { publisher.publishEvent(LeaderChangedEvent(this, it.name)) },
                { error -> log.warn("SSE error: ${error.message}",error) },
                { log.info("SSE avsluttet") }
            )
    }

    @PreDestroy
    fun onShutdown() {
        subscription.dispose()
    }

    private data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
    class LeaderChangedEvent(source: Any, val leder: String) : ApplicationEvent(source)
}