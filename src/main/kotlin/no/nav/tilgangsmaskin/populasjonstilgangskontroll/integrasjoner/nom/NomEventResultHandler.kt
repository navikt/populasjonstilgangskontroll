package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Counted
class NomEventResultHandler {
    private val log = LoggerFactory.getLogger(NomEventResultHandler::class.java)
    fun handleOK(ansattId: String, brukerId: String)  {
        log.info("Lagret fødselsnummer ${brukerId.mask()} for $ansattId OK")
    }
    fun handleFailure(ansattId: String, brukerId: String, e: Throwable)  {
        log.error("Kunne ikke lagre fødselsnummer ${brukerId.mask()} for $ansattId (${e.message})", e)
    }
}