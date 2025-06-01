package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.CONSUMER_ID
import no.nav.tilgangsmaskin.felles.utils.secureLog
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class RegelMotorLogger(private val teller: AvvisningTeller) {

    private val log = getLogger(javaClass)
    fun avvist(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        MDC.put(BESLUTNING,regel.kode)
        val fra =  MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: ""
        log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId} $fra")
        secureLog.warn("Tilgang til ${bruker.brukerId.verdi} avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} $fra")
        teller.tell(Tags.of("navn", regel.navn))
        MDC.remove(BESLUTNING)
    }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett) {
        MDC.put(BESLUTNING,OK)
        val fra =  MDC.get(CONSUMER_ID)?.let { "fra $it" } ?: ""
        log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} $fra")
        secureLog.info("${regelSett.beskrivelse} ga tilgang til  ${bruker.brukerId.verdi}  for ${ansatt.ansattId} $fra")
        MDC.remove(BESLUTNING)
    }

    fun info(message: String) {
        log.info(message)
    }
    fun warn(message: String, e: Throwable? = null) {
        log.warn(message,e)
    }

    fun trace(message: String, e: Throwable? = null) {
        log.trace(message,e)
    }

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        log.trace("Evaluerer regel: '${regel.kortNavn}' for ${ansatt.ansattId}  og ${bruker.brukerId}")
    }

    companion object   {
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
    }
}



