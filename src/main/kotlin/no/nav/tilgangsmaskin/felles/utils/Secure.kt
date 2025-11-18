package no.nav.tilgangsmaskin.felles.utils

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Auditor {
    private val secureLog = LoggerFactory.getLogger(AUDIT_LOGGER_NAME)
    fun trace(message: String) = secureLog.trace(message)
    fun warn(message: String) = secureLog.warn(message)
    fun info(message: String) = secureLog.info(message)

    companion object {
        private const val AUDIT_LOGGER_NAME = "secureLog"
    }
}