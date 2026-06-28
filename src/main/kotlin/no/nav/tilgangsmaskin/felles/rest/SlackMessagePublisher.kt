package no.nav.tilgangsmaskin.felles.rest

import com.slack.api.Slack.getInstance
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.header
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.webhook.Payload
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@Component
class SlackMessagePublisher(
    @param:Value("\${slack.webhook:}") private val webhookUrl: String) : MessagePublisher{

    private val log = getLogger(javaClass)

    override fun publish(header: String, msg: String) =
        publish(asBlocks(
            header { it.text(plainText("🚀 $header")) },
            section { alert -> alert
                .blockId("text-section")
                .text(markdownText(":info: \n$msg"))
            }))

    private fun publish(blocks: List<LayoutBlock>) =
        publish(Payload.builder().blocks(blocks).build())

    private fun publish(payload: Payload) =
        if (webhookUrl.isNotBlank()) {
            runCatching {
                log.info("Sending Slack notification $payload")
                val response = getInstance().send(webhookUrl, payload)
                log.info("Sent Slack notification, response: $response")
            }.getOrElse {
                log.warn("Failed to send Slack notification", it)
            }
        }
        else {
            log.info("Not sending Slack notification")
        }
}

interface MessagePublisher {

    fun publish(header: String, msg: String)
}
