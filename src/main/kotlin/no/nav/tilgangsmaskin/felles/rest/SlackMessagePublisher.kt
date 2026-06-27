package no.nav.tilgangsmaskin.felles.rest

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.checkerframework.checker.units.qual.m
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient.Builder

/**
 * Sends failed Kafka messages to a Slack channel after all retries are exhausted.
 * Requires `kafka.slack.webhook-url` to be configured.
 */
@Component
class SlackMessagePublisher(
    builder: Builder,
    @param:Value("\${slack.webhook:}") private val webhookUrl: String)  {

    private val client = builder.build()
    private val log = getLogger(javaClass)

     fun publish(msg: String) {
        if (webhookUrl.isBlank()) {
            log.debug("Slack webhook URL not configured, skipping Slack notification")
            return
        }

        try {
            val message = buildMessage(msg)
            client.post()
                .uri(webhookUrl)
                .body(message)
                .retrieve()
                .toBodilessEntity()

            log.info("Sent Kafka recovery notification to Slack for $msg ")
        } catch (ex: Exception) {
            log.error("Failed to send Slack notification for $msg", ex)
        }
    }

    private fun buildMessage(msg: String) =
         mapOf("text" to msg)
}