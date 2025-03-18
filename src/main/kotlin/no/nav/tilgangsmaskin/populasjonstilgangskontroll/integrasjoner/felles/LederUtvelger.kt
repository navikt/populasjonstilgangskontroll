package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.LeaderChangedEventPublisher.LeaderChangedEvent


@Service
class LederUtvelger(private val adapter: LederUtvelgerClientAdapter) : ApplicationListener<LeaderChangedEvent> {
    private val log = getLogger(LederUtvelger::class.java)

    var leder : String? = null
    val erLeder get() = leder  == InetAddress.getLocalHost().hostName

    override fun onApplicationEvent(event: LeaderChangedEvent) {
        log.info("SSE spring event $event")
        leder = event.leader
    }
}
@Component
class LederUtvelgerClientAdapter(builder: Builder, cf : LederUtvelgerConfig) : AbstractRestClientAdapter(builder.build(), cf) {
    fun leder() = get<LederUtvelgerRespons>(cfg.baseUri).name
}
data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
@Component
class LederUtvelgerConfig(@Value("\${elector.get.url}")  base: URI): AbstractRestConfig(base)

@Service
class SseService(private val webClient: WebClient.Builder) {

    fun subscribe(uri: URI) =
         webClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(LederUtvelgerRespons::class.java)
}


@Component
class SseSubscriber(private val sseService: SseService, @Value("\${elector.sse.url}") private val uri: URI, val publisher: LeaderChangedEventPublisher) {
    init {
        startSubscription()
    }
    private val log = getLogger(SseSubscriber::class.java)
    private final fun startSubscription() {
        val eventStream: Flux<LederUtvelgerRespons> = sseService.subscribe(uri)
        eventStream.subscribe { event ->
            log.info("SSE Received event: $event")
            publisher.publish(event.name)
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

