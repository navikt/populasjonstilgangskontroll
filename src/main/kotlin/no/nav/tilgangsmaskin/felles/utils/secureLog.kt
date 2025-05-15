package no.nav.tilgangsmaskin.felles.utils

import org.slf4j.LoggerFactory

object secureLog {
    private val secureLog = LoggerFactory.getLogger("secureLog")

    fun info(message: String) = secureLog.info(message)
}