package no.nav.tilgangsmaskin.felles.rest.notifikajon
import com.slack.api.Slack.getInstance
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.header
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.webhook.Payload
import com.slack.api.webhook.Payload.builder
import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackMessagePublisher.Emoji.DEV
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackMessagePublisher.Emoji.ERROR
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackMessagePublisher.Emoji.INFO
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackMessagePublisher.Emoji.PROD
import no.nav.tilgangsmaskin.felles.rest.notifikajon.SlackMessagePublisher.Emoji.WARN
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
) : MessagePublisher {

    private val env = if (isProd) PROD else DEV

    private val log = getLogger(javaClass)


    override fun error(header: String, msg: String) = publish(SlackHeader(header, env), msg, ERROR)

    override fun warn(header: String, msg: String) = publish(SlackHeader(header, env), msg, WARN)

    override fun info(header: String, msg: String) = publish(SlackHeader(header, env), msg, INFO)


     override fun publish(header: String, msg: String, vararg emojis: String) = publish(SlackHeader(header, env), msg, *emojis.map { Emoji.valueOf(it) }.toTypedArray())



        private fun publish(header: SlackHeader, msg: String, vararg emojis: Emoji) =
        publish(builder().blocks(asBlocks(
            header {
                it.text(plainText("${header.emoji.value} ${header.text}"))
            },
            section {
                it.text(markdownText("${emojis.joinToString(" ") { e -> e.value }} $msg"))
            })).build())

    private fun publish(payload: Payload) {
        if (url.isBlank()) {
            log.warn("Ingen Slack-notifikasjon: slack.webhook er tom")
        } else {
            val response = getInstance().send(url, payload)
            log.info("Slack-notifikasjon respons code={} message={}", response.code, response.message)
            if (response.code != OK.value()) {
                log.warn("Kunne ikke sende Slack-notifikasjon _(${response.code}/${response.message})_")
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
