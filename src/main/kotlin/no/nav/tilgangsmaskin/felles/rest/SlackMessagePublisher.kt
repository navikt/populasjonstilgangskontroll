package no.nav.tilgangsmaskin.felles.rest

import com.slack.api.Slack
import com.slack.api.model.block.Blocks.actions
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.divider
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.webhook.Payload
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@Component
class SlackMessagePublisher(
    @param:Value("\${slack.webhook:}") private val webhookUrl: String,
) {

    private val log = getLogger(javaClass)

    fun publish(msg: String) =
        publish(asBlocks(
            section { section -> section
                .blockId("intro-section")
                .text(markdownText(msg))
            }))
    

    fun publish(blocks: List<LayoutBlock>) {
        publish(Payload.builder().blocks(blocks).build())
    }

    open fun publish(payload: Payload) {
        if (webhookUrl.isNotBlank()) {
            runCatching {
                log.info("Sending Slack notification")
                val response = Slack.getInstance().send(webhookUrl, payload)
                log.info("Sent Slack notification, response: $response")
            }.getOrElse {
                log.error("Failed to send Slack notification", it)
            }
        }
    }

}