package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher

class SlackApplicationReadyNotifierTest : BehaviorSpec({
    Given("ApplicationReadyEvent håndteres") {
        val publisher = mockk<MessagePublisher>(relaxed = true)
        val notifier = SlackApplicationReadyNotifier(publisher, "tilgangsmaskin", "dev-gcp", "app:1.2.3")

        When("appen er klar") {
            notifier.onApplicationReady()

            Then("publiseres startup-melding til Slack") {
                verify {
                    publisher.info(
                        "Applikasjon klar",
                        "tilgangsmaskin er startet i cluster 'dev-gcp' med image 'app:1.2.3'"
                    )
                }
            }
        }
    }
})
