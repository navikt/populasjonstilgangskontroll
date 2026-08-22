package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher, env: Environment ) {

    private val pod = env.getProperty("hostname") ?: "unknown"

    private val image = env.getRequiredProperty("nais.app.image")
    private val log = getLogger(javaClass)


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() =
        runCatching {
            publisher.info("En instans av _${pod}_ er klar", "Startet  _${pod}_ for image _${image}_")
            log.info("Startet $pod for image $image")
        }.onFailure {
            log.warn("Feilet ved sending av startup-notifikasjon", it)
        }
}
