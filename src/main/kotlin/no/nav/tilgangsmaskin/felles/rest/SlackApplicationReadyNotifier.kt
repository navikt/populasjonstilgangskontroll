package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher, env: Environment ) {
    private val app = env.getRequiredProperty("spring.application.name")
    private val image = env.getRequiredProperty("nais.app.image")


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        publisher.info("Applikasjon klar", "$app er startet i cluster '$current' med image '$image'")
    }
}
