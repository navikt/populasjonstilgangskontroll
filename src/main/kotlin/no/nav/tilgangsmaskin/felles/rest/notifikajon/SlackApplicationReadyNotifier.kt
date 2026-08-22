package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
     publisher: MessagePublisher, env: Environment) : EnvironmentAwarePublishingNotifier(publisher, env) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() =
        runCatching {
            publisher.publish("$pod startet", "Startet  _${pod}_ for image _${image}_",":rocket:",":tada:",":confetti-2:",":partyparrot:")
            log.info("Startet $pod for image $image")
        }.onFailure {
            log.warn("Feilet ved sending av startup-notifikasjon", it)
        }
}
