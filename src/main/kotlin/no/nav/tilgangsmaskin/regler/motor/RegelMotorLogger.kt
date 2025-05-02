package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.upcase
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
@Counted
class RegelMotorLogger {

    private val secureLogger = getLogger("secureLog")
    private val log = getLogger(javaClass)
    fun avvist(ansattId: AnsattId, brukerId: BrukerId, regel: Regel) {
        log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for $ansattId")
        secureLogger.info("Tilgang til $brukerId avvist av regel '${regel.kortNavn}' (${regel.begrunnelse}) for $ansattId")
    }

    fun ok(type: RegelType, ansattId: AnsattId) {
        log.info("${type.beskrivelse.upcase()} ga tilgang for $ansattId")
    }

    fun sjekker(regel: Regel, ansatt: Ansatt, bruker: Bruker) {
        log.trace("Sjekker regel: '${regel.kortNavn}' for ${ansatt.ansattId}  og ${bruker.brukerId}")

    }
}