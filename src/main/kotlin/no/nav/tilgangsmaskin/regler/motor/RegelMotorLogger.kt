package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.upcase
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
@Counted
class RegelMotorLogger {

    private val secureLogger = getLogger("secureLog")
    private val log = getLogger(javaClass)
    fun avvist(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId}")
        secureLogger.info("Tilgang til ${bruker.brukerId.verdi} avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId}")
    }

    fun ok(ansatt: Ansatt, regelSett: RegelSett) {
        log.info("${regelSett.type.beskrivelse.upcase()} ga tilgang for ${ansatt.ansattId}")
    }

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        log.trace("Evaluerer regel: '${regel.kortNavn}' for ${ansatt.ansattId}  og ${bruker.brukerId}")
    }
}