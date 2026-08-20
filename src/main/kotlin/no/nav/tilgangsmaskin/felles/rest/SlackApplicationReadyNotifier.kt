package no.nav.tilgangsmaskin.felles.rest

import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.DEV
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.PROD
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SlackApplicationReadyNotifier(
    private val publisher: MessagePublisher, env: Environment ) {
    private val app = env.getRequiredProperty("spring.application.name")
    private val image = env.getRequiredProperty("nais.app.image")
    private val log = getLogger(javaClass)
    private val emoji = if (isProd) PROD else DEV


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        publisher.publish("Applikasjon klar", "$app er startet i  _${current.name}_ med image _${image}_",emoji).also {
            log.info("Applikasjon klar: $app er startet i  _${current.name}_ med image _${image}_")
        }
    }
}
