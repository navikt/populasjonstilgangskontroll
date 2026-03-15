package no.nav.tilgangsmaskin.felles.utils

import no.nav.boot.conditionals.ConditionalOnGCP
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@Component
@ConditionalOnGCP
class SecureAuditor : AbstractAuditor(AUDIT_LOGGER_NAME) {
    private companion object {
        private const val AUDIT_LOGGER_NAME = "secureLog"
    }
}

@Fallback
@Component
class DevAuditor : AbstractAuditor(DevAuditor::class.java.simpleName)

abstract class AbstractAuditor(loggerName: String) : Auditor {
    protected val logger = getLogger(loggerName)
    override fun trace(message: String,t: Throwable?) = logger.trace(message,t)
    override fun warn(message: String, t: Throwable?) = logger.warn(message, t)
    override fun info(message: String,t: Throwable?) = logger.info(message,t)
}

interface Auditor {
    fun trace(message: String, t: Throwable? = null)
    fun warn(message: String, t: Throwable? = null)
    fun info(message: String, t: Throwable? = null)
}
