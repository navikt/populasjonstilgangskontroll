package no.nav.tilgangsmaskin.felles.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class SlackApplicationReadyNotifierTest : BehaviorSpec({
    val header = "Applikasjon klar"
    val message = "tilgangsmaskin er startet i cluster 'dev-gcp' med image 'app:1.2.3'"

    Given("ApplicationReadyEvent håndteres") {
        When("denne instansen reserverer nøkkelen først") {
            val publisher = mockk<MessagePublisher>(relaxed = true)
            val valkey = mockk<StringRedisTemplate>()
            val valueOps = mockk<ValueOperations<String, String>>()
            every { valkey.opsForValue() } returns valueOps
            every { valueOps.setIfAbsent(any(), any(), any<Duration>()) } returns true
            val notifier = SlackApplicationReadyNotifier(publisher, valkey, "tilgangsmaskin", "dev-gcp", "app:1.2.3")

            notifier.onApplicationReady()

            Then("publiseres startup-melding til Slack") {
                verify(exactly = 1) { publisher.info(header, message) }
            }
        }

        When("nøkkelen allerede er reservert av en annen instans") {
            val publisher = mockk<MessagePublisher>(relaxed = true)
            val valkey = mockk<StringRedisTemplate>()
            val valueOps = mockk<ValueOperations<String, String>>()
            every { valkey.opsForValue() } returns valueOps
            every { valueOps.setIfAbsent(any(), any(), any<Duration>()) } returns false
            val notifier = SlackApplicationReadyNotifier(publisher, valkey, "tilgangsmaskin", "dev-gcp", "app:1.2.3")

            notifier.onApplicationReady()

            Then("publiseres ingen startup-melding") {
                verify(exactly = 0) { publisher.info(any(), any()) }
            }
        }
    }
})
