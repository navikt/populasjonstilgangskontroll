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
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.DEV
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.ERROR
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.INFO
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.PROD
import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji.WARN
import no.nav.tilgangsmaskin.felles.utils.LeaderAware
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
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

    private val emoji = if (isProd) PROD else DEV

    private val log = getLogger(javaClass)


    override fun error(header: String, msg: String) = publish(SlackHeader(header, emoji), msg, ERROR)

    override fun warn(header: String, msg: String) = publish(SlackHeader(header, emoji), msg, WARN)

    override fun info(header: String, msg: String) = publish(SlackHeader(header, emoji), msg, INFO)


     private fun publish(header: SlackHeader, msg: String, vararg emojis: Emoji) =
        publish(builder().blocks(asBlocks(
            header {
                it.text(plainText("${header.emoji.value}  ${header.text}"))
            },
            section {
                it.text(markdownText("${emojis.joinToString(" ") { e -> e.value }} $msg"))
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

    private data class SlackHeader(val text: String, val emoji: Emoji = INFO)

    private enum class Emoji(val value: String) {
        WARN(":warn:"),
        ERROR(":error:"),
        INFO(":info:"),
        DEV(":dev:"),
        PROD(":production:"),

    }
}
