package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.notifikasjon.SlackApplicationReadyNotifier
import no.nav.tilgangsmaskin.felles.rest.notifikasjon.MessagePublisher
import org.springframework.mock.env.MockEnvironment

class SlackApplicationReadyNotifierTest : BehaviorSpec({
    val appName = "tilgangsmaskin"
    val image = "app:1.2.3"

    Given("ApplicationReadyEvent håndteres") {
            When("denne instansen reserverer nøkkelen først") {
            val publisher = mockk<MessagePublisher>(relaxed = true)
                val env = MockEnvironment()
                env.setProperty("spring.application.name", appName)
                env.setProperty("hostname", "hostname")
                env.setProperty("nais.app.image", image)
                val notifier = SlackApplicationReadyNotifier(publisher, env)
                notifier.onApplicationReady()

                Then("publiseres en startup-melding til Slack") {
                    verify(exactly = 1) { publisher.publish(any(), any(),*anyVararg()) }
                }
            }
    }
})
