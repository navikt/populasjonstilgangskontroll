package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
@Counted
class RegelMotorLogger {

    private val secureLog = getLogger("secureLog")
    private val log = getLogger(javaClass)
    fun avvist(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        MDC.put(BESLUTNING,regel.kode)
        log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId}")
        secureLog.info("Tilgang til ${bruker.brukerId.verdi} avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId}")
    }

    fun ok(ansatt: Ansatt, regelSett: RegelSett) {
        MDC.put(BESLUTNING,"TILGANG_OK")
        log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId}")
    }

    fun warn(message: String, e: Throwable? = null) {
        log.warn(message,e)
    }

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        log.trace("Evaluerer regel: '${regel.kortNavn}' for ${ansatt.ansattId}  og ${bruker.brukerId}")
    }

    companion object   {
        private const val BESLUTNING = "beslutning"
    }
}