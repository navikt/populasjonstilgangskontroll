package no.nav.tilgangsmaskin.felles.utils

import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class Auditor {
    private val secureLog = getLogger(AUDIT_LOGGER_NAME)
    fun trace(message: String,t: Throwable? = null) = secureLog.trace(message,t)
    fun warn(message: String, t: Throwable? = null) = secureLog.warn(message, t)
    fun info(message: String,t: Throwable? = null) = secureLog.info(message,t)

    companion object {
        private const val AUDIT_LOGGER_NAME = "secureLog"
    }
}