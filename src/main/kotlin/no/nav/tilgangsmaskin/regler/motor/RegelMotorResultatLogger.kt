package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.upcase
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
@Counted
class RegelMotorResultatLogger {

    private val secureLogger = getLogger("secureLog")
    private val log = getLogger(javaClass)
    fun avvist(ix: String, ansattId: AnsattId, brukerId: BrukerId, regel: Regel) {
        log.warn("[#$ix] Tilgang  avvist. (${regel.begrunnelse}) for $ansattId")
        secureLogger.info("[#$ix] Tilgang til $brukerId avvist av regel '${regel.kortNavn}' (${regel.begrunnelse}) for $ansattId")
    }

    fun ok(type: RegelType, ansattId: AnsattId, brukerId: BrukerId) {
        log.info(CONFIDENTIAL, "${type.beskrivelse.upcase()} ga tilgang til $brukerId for $ansattId")
    }
}