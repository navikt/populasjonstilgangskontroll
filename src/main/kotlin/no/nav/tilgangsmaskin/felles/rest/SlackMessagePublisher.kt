package no.nav.tilgangsmaskin.felles.rest
import com.slack.api.Slack.getInstance
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.header
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.webhook.Payload
import com.slack.api.webhook.Payload.builder
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Component

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@Component
class SlackMessagePublisher(
    @param:Value("\${slack.webhook:}") private val url: String) : MessagePublisher {

    private val log = getLogger(javaClass)

    override fun warn(header: String, msg: String) = publish(":warn:", header, msg)

    override fun info(header: String, msg: String) = publish(":rocket:",header, msg)

    private fun publish(emoji: String, header: String, msg: String) =
        publish(builder().blocks(asBlocks(
            header { it.text(plainText("$emoji $header")) },
            section { info -> info.text(markdownText(msg)) })).build())

    private fun publish(payload: Payload) =
            if (url.isBlank()) {
                log.info("Ingen Slack notifikasjon")
            }
            else {
                with(getInstance().send(url, payload)) {
                    if (code != OK.value()) {
                        log.warn("Kunne ikke sende Slack notifikasjon _($code/$message)_")
                    }
                    else  {
                        log.info("Sendte Slack notifikasjon OK")
                    }
                }
            }
}
