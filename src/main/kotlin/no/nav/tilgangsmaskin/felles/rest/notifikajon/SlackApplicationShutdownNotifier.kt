package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationShutdownNotifier(
    private val publisher: MessagePublisher,
    env: Environment,
) {

    private val pod = env.getProperty("hostname") ?: "unknown"
    private val image = env.getRequiredProperty("nais.app.image")
    private val log = getLogger(javaClass)

    @EventListener(ContextClosedEvent::class)
    fun onApplicationShutdown() {

        runCatching {
            publisher.info(" _${pod}_ stenges ned", "Stopper $pod for image _${image}_")
            log.info("Stopper $pod for image _${image}_")
        }.onFailure {
            log.warn("Feilet ved sending av shutdown-notifikasjon", it)
        }
    }
}
