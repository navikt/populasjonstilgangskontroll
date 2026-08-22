package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment

abstract class EnvironmentAwarePublishingNotifier(
    protected val publisher: MessagePublisher, env: Environment) {
    protected val pod = env.getProperty("hostname") ?: "unknown"
    protected val image = env.getRequiredProperty("nais.app.image")
    protected val log = LoggerFactory.getLogger(javaClass)
}