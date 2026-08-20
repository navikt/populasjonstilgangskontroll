package no.nav.tilgangsmaskin.felles.utils

import no.nav.tilgangsmaskin.felles.rest.SlackMessagePublisher.Emoji
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@FunctionalInterface
interface MessagePublisher {

    private val logger: Logger
        get() = getLogger(MessagePublisher::class.java)

    fun info(header: String, msg: String) = logger.info("$header: $msg")
    fun warn(header: String, msg: String) = logger.warn("$header: $msg")
    fun error(header: String, msg: String) = logger.error("$header: $msg")
    fun publish(header: String, msg: String, vararg emoji: Emoji) = logger.info("$header: $msg")
}

@Component
@Fallback
class LoggingMessagePublisher : MessagePublisher