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
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.netty.http.client.PrematureCloseException
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
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
        subscription = Mono.delay(Duration.ofMillis(500))
            .flatMapMany {
                builder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux<LederUtvelgerRespons>()
            }
            .doOnError { error -> log.error("SSE connection failed permanently: ${error.message}", error) }
            .doOnSubscribe { log.info("SSE subscribe") }
            .doOnNext { log.info("SSE next: $it ") }
            .retryWhen(
                Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(30))
                    .filter { error ->
                        error is WebClientRequestException ||
                                error is PrematureCloseException ||
                                error.cause is PrematureCloseException
                    }
                .doBeforeRetry { log.info("SSE retry ${it.failure().message}") }
                .doAfterRetry { log.info("SSE connection retry after ${it.totalRetries()} attempts") }
            )
            .subscribe(
                { publisher.publishEvent(LeaderChangedEvent(this, it.name)) },
                { error -> log.warn("SSE error: ${error.message}", error) }
            )
    }

    @PreDestroy
    fun onShutdown() {
        subscription.dispose()
    }

    private data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
    class LeaderChangedEvent(source: Any, val leder: String) : ApplicationEvent(source)
}