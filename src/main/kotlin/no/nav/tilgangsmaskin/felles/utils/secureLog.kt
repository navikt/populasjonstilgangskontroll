package no.nav.tilgangsmaskin.felles.utils

import org.slf4j.LoggerFactory

object secureLog {
    private val secureLog = LoggerFactory.getLogger("secureLog")

    fun trace(message: String) = secureLog.trace(message)
    fun debug(message: String) = secureLog.debug(message)
    fun warn(message: String) = secureLog.warn(message)
    fun error(message: String) = secureLog.error(message)
    fun info(message: String) = secureLog.info(message)
}