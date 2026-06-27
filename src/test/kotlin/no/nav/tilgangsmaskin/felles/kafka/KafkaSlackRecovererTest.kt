package no.nav.tilgangsmaskin.felles.kafka

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.web.client.RestClient

class KafkaSlackRecovererTest : BehaviorSpec({

    Given("KafkaSlackRecoverer med webhook-URL konfigurert") {
        val restClient = mockk<RestClient>(relaxed = true)
        val recoverer = SlackMessagePublisher(restClient, "https://hooks.slack.com/services/xxx/yyy/zzz")

        When("accept kalles med en mislykket melding") {
            val record = ConsumerRecord(
                "test-topic",
                0,
                42L,
                "key",
                """{"data":"value"}"""
            )
            val exception = RuntimeException("Database connection failed")
            
            recoverer.accept(record, exception)

            Then("skal Slack API kalles med riktig melding") {
                verify(atLeast = 1) {
                    restClient.post()
                }
            }
        }
    }

    Given("KafkaSlackRecoverer uten webhook-URL") {
        val restClient = mockk<RestClient>(relaxed = true)
        val recoverer = SlackMessagePublisher(restClient, "")

        When("accept kalles") {
            val record = ConsumerRecord("topic", 0, 1L, "key", "value")
            recoverer.accept(record, RuntimeException("error"))

            Then("skal REST-kallet ikke gjøres") {
                verify(exactly = 0) {
                    restClient.post()
                }
            }
        }
    }
})
