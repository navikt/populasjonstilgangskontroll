package no.nav.tilgangsmaskin.felles.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Sends failed Kafka messages to a Slack channel after all retries are exhausted.
 * Requires `kafka.slack.webhook-url` to be configured.
 */
@Component
class KafkaSlackRecoverer(
    private val restClient: RestClient,
    @Value("\${slack.webhook:}") private val webhookUrl: String
) : ConsumerRecordRecoverer {

    private val log = getLogger(javaClass)

    override fun accept(record: ConsumerRecord<*, *>, exception: Exception) {
        if (webhookUrl.isBlank()) {
            log.debug("Slack webhook URL not configured, skipping Slack notification")
            return
        }

        try {
            val message = buildMessage(record, exception)
            restClient.post()
                .uri(webhookUrl)
                .body(message)
                .retrieve()
                .toBodilessEntity()

            log.info("Sent Kafka recovery notification to Slack for topic=${record.topic()} partition=${record.partition()} offset=${record.offset()}")
        } catch (ex: Exception) {
            log.error("Failed to send Slack notification for failed Kafka message", ex)
        }
    }

    private fun buildMessage(record: ConsumerRecord<*, *>, exception: Exception): SlackMessage {
        val errorDetails = """
            *Topic:* ${record.topic()}
            *Partition:* ${record.partition()}
            *Offset:* ${record.offset()}
            *Error:* ${exception.javaClass.simpleName}
            *Message:* ${exception.message ?: "Unknown error"}
        """.trimIndent()

        return SlackMessage(
            blocks = listOf(
                SlackBlock(
                    type = "section",
                    text = SlackText(
                        type = "mrkdwn",
                        text = ":warning: *Kafka Message Recovery Failed*\n$errorDetails"
                    )
                )
            )
        )
    }
}

data class SlackMessage(val blocks: List<SlackBlock>)

data class SlackBlock(
    val type: String,
    val text: SlackText
)

data class SlackText(
    val type: String,
    val text: String
)
