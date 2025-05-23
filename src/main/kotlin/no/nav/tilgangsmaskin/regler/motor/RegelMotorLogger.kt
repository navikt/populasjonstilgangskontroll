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
        log.warn("Tilgang avvist av regel '${regel.kortNavn}'. (${regel.begrunnelse}) for ${ansatt.ansattId}")
        secureLog.warn("Tilgang til ${bruker.brukerId.verdi} avvist av regel '${regel.kortNavn}' for ${ansatt.ansattId} fra ${MDC.get(CONSUMER_ID)}")
        teller.tell(Tags.of("navn", regel.navn))
        MDC.remove(BESLUTNING)
    }

    fun ok(ansatt: Ansatt, bruker: Bruker,regelSett: RegelSett) {
        MDC.put(BESLUTNING,OK)
        log.info("${regelSett.beskrivelse} ga tilgang for ${ansatt.ansattId} fra ${MDC.get(CONSUMER_ID)}")
        secureLog.info("${regelSett.beskrivelse} ga tilgang til  ${bruker.brukerId.verdi}  for ${ansatt.ansattId} fra ${MDC.get(CONSUMER_ID)}")
        MDC.remove(BESLUTNING)
    }

    fun info(message: String, e: Throwable? = null) {
        log.warn(message,e)
    }

    fun evaluerer(ansatt: Ansatt, bruker: Bruker, regel: Regel) {
        log.trace("Evaluerer regel: '${regel.kortNavn}' for ${ansatt.ansattId}  og ${bruker.brukerId}")
    }

    companion object   {
        private const val BESLUTNING = "beslutning"
        private const val OK = "TILGANG_OK"
    }
}



