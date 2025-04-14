package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
@Counted
class NomEventResultHandler {
    private val log = getLogger(javaClass)
    fun handleOK(ansattId: String, brukerId: String) {
        log.info("Lagret fødselsnummer ${brukerId.maskFnr()} for $ansattId OK")
    }

    fun handleFailure(ansattId: String, brukerId: String, e: Throwable) {
        log.error("Kunne ikke lagre fødselsnummer ${brukerId.maskFnr()} for $ansattId (${e.message})", e)
    }
}