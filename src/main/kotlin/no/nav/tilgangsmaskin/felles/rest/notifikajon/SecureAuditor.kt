package no.nav.tilgangsmaskin.felles.rest.notifikajon

import no.nav.boot.conditionals.ConditionalOnGCP
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ConditionalOnGCP
class SecureAuditor(logger: Logger = LoggerFactory.getLogger("secureLog")) : AbstractAuditor(logger)