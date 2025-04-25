package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
@Counted
class NomHendelseResultatLogger {
    private val log = getLogger(javaClass)
    fun ok(ansattId: String, brukerId: String) {
        log.info("Lagret brukerId ${brukerId.maskFnr()} for $ansattId OK")
    }

    fun feilet(ansattId: String, brukerId: String, e: Throwable) {
        log.error("Kunne ikke lagre brukerId ${brukerId.maskFnr()} for $ansattId (${e.message})", e)
    }
}