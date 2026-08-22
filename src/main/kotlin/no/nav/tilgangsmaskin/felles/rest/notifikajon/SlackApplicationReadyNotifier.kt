package no.nav.tilgangsmaskin.felles.rest.notifikajon

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher, env: Environment ) {

    private val pod = env.getProperty("hostname") ?: "unknown"

    private val app = env.getRequiredProperty("spring.application.name")
    private val image = env.getRequiredProperty("nais.app.image")
    private val log = getLogger(javaClass)


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        publisher.info("En instans av $app er klar", "Startet $pod i  _${current.name}_ med image _${image}_").also {
            log.info("Applikasjon klar: $app er startet $pod i  _${current.name}_ med image _${image}_")
        }
    }
}
