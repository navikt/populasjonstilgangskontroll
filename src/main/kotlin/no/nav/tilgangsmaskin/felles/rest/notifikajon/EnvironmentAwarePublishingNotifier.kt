package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.env.Environment

abstract class EnvironmentAwarePublishingNotifier(
    protected val publisher: MessagePublisher, env: Environment) {
    protected val pod = env.getProperty("hostname") ?: "localhost"
    protected val image = env.getRequiredProperty("nais.app.image")
    protected val log = getLogger(javaClass)
}