package no.nav.tilgangsmaskin.felles.rest.notifikajon

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@Fallback
@Component
class LocalAuditor(logger: Logger = LoggerFactory.getLogger(LocalAuditor::class.java.simpleName)) : AbstractAuditor(logger)