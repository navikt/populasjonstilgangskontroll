package no.nav.tilgangsmaskin.felles.rest.notifikajon

import no.nav.boot.conditionals.ConditionalOnGCP
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@ConditionalOnGCP
class SecureAuditor(logger: Logger = getLogger("secureLog")) : AbstractAuditor(logger)

@Fallback
@Component
class LocalAuditor(logger: Logger = getLogger(LocalAuditor::class.java.simpleName)) : AbstractAuditor(logger)

abstract class AbstractAuditor(protected val logger: Logger) : Auditor {
    override fun info(message: String, t: Throwable?) = t?.let { logger.info(message, it) } ?: logger.info(message)
}

@FunctionalInterface
interface Auditor {
    fun info(message: String, t: Throwable? = null)
}
