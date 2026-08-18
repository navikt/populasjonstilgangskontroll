package no.nav.tilgangsmaskin.felles.rest
import com.slack.api.Slack.getInstance
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.header
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.BlockCompositions.plainText
import com.slack.api.webhook.Payload
import com.slack.api.webhook.Payload.builder
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.utils.MessagePublisher
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.current
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus.OK
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration.ofSeconds

/**
 * Publiserer meldinger til Slack via incoming webhook.
 */
@ConditionalOnNotProd
class SlackMessagePublisher(
    env: Environment,
    private val valkey: StringRedisTemplate,
    @param:Value("\${slack.webhook:}") private val url: String,
) : MessagePublisher {

    private val app = env.getRequiredProperty("spring.application.name")
    private val image = env.getRequiredProperty("nais.app.image")
    private val key = "${current.name}::${app}::${image}"

    private val log = getLogger(javaClass)

    override fun warn(header: String, msg: String) =  publish(":warn:", header, msg)

    override fun info(header: String, msg: String) = publish(":rocket:",header, msg)


    private fun publish(emoji: String, header: String, msg: String) =
        publish(key,builder().blocks(asBlocks(
            header { it.text(plainText("$emoji $header")) },
            section { info -> info.text(markdownText(msg)) })).build())

    private fun publish(key: String,payload: Payload) =
        if (erFørste(key + payload.hashCode())) {
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
    else Unit

    private fun erFørste(key: String): Boolean =
        runCatching {
            valkey.opsForValue().setIfAbsent(key, "sent", ofSeconds(10)) == true
        }.onFailure {
            log.warn("Kunne ikke reservere startup-slack nøkkel i Valkey", it)
        }.getOrDefault(true)
}
