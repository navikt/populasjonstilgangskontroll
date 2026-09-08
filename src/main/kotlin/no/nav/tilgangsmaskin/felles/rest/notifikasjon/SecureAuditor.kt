package no.nav.tilgangsmaskin.felles.rest.notifikasjon

import no.nav.boot.conditionals.ConditionalOnGCP
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@ConditionalOnGCP
@Component
class SecureAuditor(
    @Value("\${logging.secure-log-name}") loggerName: String) : AbstractAuditor(getLogger(loggerName))