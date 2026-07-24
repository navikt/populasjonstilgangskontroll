package no.nav.tilgangsmaskin.felles

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
    override fun info(message: String, t: Throwable?) = logger.info(message, t)
}

@FunctionalInterface
interface Auditor {
    fun info(message: String, t: Throwable? = null)
}
