package no.nav.tilgangsmaskin.felles.rest
import com.slack.api.Slack.getInstance
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.header
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.webhook.Payload
import com.slack.api.webhook.Payload.builder
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.SlackEmoji.ERROR
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.SlackEmoji.INFO
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.SlackEmoji.WARN
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.OK

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@ConditionalOnGCP
class SlackMessagePublisher(
    @param:Value("\${slack.webhook:}") private val url: String,
) : MessagePublisher, LeaderAware(true) {


    private val log = getLogger(javaClass)

    override fun error(header: String, msg: String) = publish(ERROR, header, msg)

    override fun warn(header: String, msg: String) = publish(WARN, header, msg)

    override fun info(header: String, msg: String) = publish(INFO, header, msg)


    private fun publish(emoji: SlackEmoji, header: String, msg: String) =
        publish(builder().blocks(asBlocks(
            header {
                it.text(plainText("${emoji.value} $header"))
            },
            section {
                it.text(markdownText(msg))
            })).build())

    private fun publish(payload: Payload) =
        somLeder {
            if (url.isBlank()) {
                log.info("Ingen Slack-notifikasjon")
            }
            else {
                log.trace("Sender Slack-notifikasjon til {}", url)
                with(getInstance().send(url, payload)) {
                    if (code != OK.value()) {
                        log.warn("Kunne ikke sende Slack-notifikasjon _($code/$message)_")
                    }
                    else  {
                        log.trace("Sendte Slack-notifikasjon OK")
                    }
                }
            }
        }

    private enum class SlackEmoji(val value: String) {
        WARN(":warn:"),
        ERROR(":error:"),
        INFO(":info:"),
    }
}
