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
class LocalAuditor : AbstractAuditor(LocalAuditor::class.java.simpleName)

abstract class AbstractAuditor(loggerName: String) : Auditor {
    private val logger = getLogger(loggerName)
    override fun info(message: String,t: Throwable?) = logger.info(message,t)
}

@FunctionalInterface
interface Auditor {
    fun info(message: String, t: Throwable? = null)
}
