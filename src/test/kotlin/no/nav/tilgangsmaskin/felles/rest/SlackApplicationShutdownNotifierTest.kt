package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackApplicationShutdownNotifier
import no.nav.tilgangsmaskin.felles.rest.notifikajon.MessagePublisher
import org.springframework.core.env.Environment

class SlackApplicationShutdownNotifierTest : BehaviorSpec({
    val appName = "tilgangsmaskin"
    val image = "app:1.2.3"
    val podName = "pod-1"

    Given("ContextClosedEvent håndteres") {
        When("applikasjonen stenger ned") {
            val publisher = mockk<MessagePublisher>(relaxed = true)
            val env = mockk<Environment>()
            every { env.getProperty("hostname") } returns podName

            every { env.getRequiredProperty("nais.app.image") } returns image
            val notifier = SlackApplicationShutdownNotifier(publisher, env)

            notifier.onApplicationShutdown()

            Then("publiseres en shutdown-melding til Slack") {
                verify(exactly = 1) { publisher.publish(any(), any(), any()) }
            }
        }
    }
})
