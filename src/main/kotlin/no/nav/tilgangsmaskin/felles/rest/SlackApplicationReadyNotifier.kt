package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher,
    @param:Value("\${info.name:\${spring.application.name:populasjonstilgangskontroll}}") private val appName: String,
    @param:Value("\${info.cluster:local}") private val cluster: String,
    @param:Value("\${info.image:unknown}") private val image: String,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() =
        publisher.info("Applikasjon klar", "$appName er startet i cluster '$cluster' med image '$image'")
}
