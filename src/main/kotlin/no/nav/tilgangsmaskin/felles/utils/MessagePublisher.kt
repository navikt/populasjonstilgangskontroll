package no.nav.tilgangsmaskin.felles.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

@FunctionalInterface
interface MessagePublisher {

    private val logger: Logger
        get() = getLogger(MessagePublisher::class.java)

    fun info(header: String, msg: String) = logger.info("$header: $msg")

    fun warn(header: String, msg: String) = logger.warn("$header: $msg")

}