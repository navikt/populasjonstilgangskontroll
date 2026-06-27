package no.nav.tilgangsmaskin.felles.rest

import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@Component
class SlackMessagePublisher(
    @param:Value("\${slack.webhook:}") private val webhookUrl: String,
) {

    private val client = RestClient.builder().build()
    private val log = getLogger(javaClass)

    fun publish(msg: String) =
        publish(SlackMessagePayload(msg))

    fun publish(message: SlackMessagePayload) {
        if (webhookUrl.isBlank()) {
            log.debug("Slack webhook URL not configured, skipping Slack notification")
            return
        }

        try {
            log.info("Sending Slack notification")
            client.post()
                .uri(webhookUrl)
                .body(message)
                .retrieve()
                .toBodilessEntity()
            log.info("Sent Slack notification")
        } catch (ex: Exception) {
            log.error("Failed to send Slack notification", ex)
        }
    }
}