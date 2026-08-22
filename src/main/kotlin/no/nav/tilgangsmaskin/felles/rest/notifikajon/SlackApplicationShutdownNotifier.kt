package no.nav.tilgangsmaskin.felles.rest.notifikajon

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
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
    private val app = env.getRequiredProperty("spring.application.name")
    private val image = env.getRequiredProperty("nais.app.image")
    private val log = getLogger(javaClass)

    @EventListener(ContextClosedEvent::class)
    fun onApplicationShutdown() {
        log.info("ContextClosedEvent mottatt: $app stopper i _${current.name}_ med image _${image}_")
        runCatching {
            publisher.info("En instans av $app stenges ned", "Stopper $pod i  _${current.name}_ med image _${image}_")
        }.onFailure {
            log.warn("Feilet ved sending av shutdown-notifikasjon", it)
        }
    }
}
