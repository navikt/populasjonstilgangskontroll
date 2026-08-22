package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackApplicationReadyNotifier
import no.nav.tilgangsmaskin.felles.rest.notifikajon.MessagePublisher
import org.springframework.core.env.Environment

class SlackApplicationReadyNotifierTest : BehaviorSpec({
    val appName = "tilgangsmaskin"
    val image = "app:1.2.3"

    Given("ApplicationReadyEvent håndteres") {
            When("denne instansen reserverer nøkkelen først") {
            val publisher = mockk<MessagePublisher>(relaxed = true)
                val env = mockk<Environment>()
                every { env.getProperty("spring.application.name") } returns appName
                every { env.getProperty("hostname") } returns "hostname"
                every { env.getRequiredProperty("nais.app.image") } returns image
                val notifier = SlackApplicationReadyNotifier(publisher, env)

                notifier.onApplicationReady()

                Then("publiseres en startup-melding til Slack") {
                    verify(exactly = 1) { publisher.publish(any(), any(),any()) }
                }
            }
    }
})
