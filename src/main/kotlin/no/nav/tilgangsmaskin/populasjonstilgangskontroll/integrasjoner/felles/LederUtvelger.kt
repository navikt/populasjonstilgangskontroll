package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomHendelseKonsument
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.Builder
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime

@Service
class LederUtvelger(private val adapter: LederUtvelgerClientAdapter) {
    val erLeder get() = adapter.leder() == InetAddress.getLocalHost().hostName
}
@Component
class LederUtvelgerClientAdapter(builder: Builder, cf : LederUtvelgerConfig) : AbstractRestClientAdapter(builder.build(), cf) {
    fun leder() = get<LederUtvelgerRespons>(cfg.baseUri).name
}
private data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
@Component
class LederUtvelgerConfig(@Value("\${elector.get.url}")  base: URI): AbstractRestConfig(base)

@Service
class SseService(private val webClient: WebClient.Builder) {

    fun subscribe(uri: URI) =
         webClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(Any::class.java)
}


@Component
class SseSubscriber(private val sseService: SseService, @Value("\${elector.sse.url}") private val uri: URI) {
    init {
        startSubscription()
    }
    private val log = getLogger(SseSubscriber::class.java)
    private final fun startSubscription() {
        val eventStream: Flux<Any> = sseService.subscribe(uri)
        eventStream.subscribe { event ->
            log.info("SSE Received event: $event")
        }
    }
}

