package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationShutdownNotifier(publisher: MessagePublisher, env:Environment,
) : EnvironmentAwarePublishingNotifier(publisher, env) {

    @EventListener(ContextClosedEvent::class)
    fun onApplicationShutdown() =
        runCatching {
            publisher.info(" $pod stopper", "Stopper  _${pod}_ for image _${image}_")
            log.info("Stopper $pod for image $image")
        }.onFailure {
            log.warn("Feilet ved sending av shutdown-notifikasjon", it)
        }
}