package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Counted
class OverstyringResultatHandler {
    private val log = LoggerFactory.getLogger(OverstyringResultatHandler::class.java)
    fun gyldig(ansattId: AnsattId, brukerId: BrukerId, diff: String) = true.also {
        log.trace("Overstyring er gyldig i $diff til for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
    }
    fun utgått(ansattId: AnsattId, brukerId: BrukerId, diff: String) = false.also {
        log.warn("Overstyring har gått ut på tid for $diff siden for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
    }
    fun ingen(ansattId: AnsattId, brukerId: BrukerId) = false.also {
        log.trace("Ingen overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' ble funnet i databasen")
    }
    fun avvist(ansattId: AnsattId, brukerId: BrukerId) =
        log.error("Overstyring er avvist av kjerneregler for '${ansattId.verdi}' og bruker '${brukerId.mask()})")

    fun overstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        log.info("Overstyring er gjort for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
}