package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import io.micrometer.core.annotation.Counted
import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.diffFromNow
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.isBeforeNow
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Cacheable(OVERSTYRING)
@Transactional
@Timed
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste, private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor, private val handler: OverstyringHandler = OverstyringHandler()) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        with(adapter.gjeldendeOverstyringGyldighetDato(ansattId.verdi, brukerId.verdi, bruker.bruker(brukerId).historiskeIdentifikatorer.map { it.verdi })) {
            when {
                this == null -> handler.ingen(ansattId, brukerId)
                isBeforeNow() -> handler.utgått(ansattId, brukerId, diffFromNow())
                else -> handler.gyldig(ansattId, brukerId, diffFromNow())
            }
        }

    fun overstyr(ansattId: AnsattId,data: OverstyringData)  =
         runCatching {
                log.info("Sjekker kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.mask()}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(data.brukerId))
                adapter.overstyr(ansattId.verdi, data).also {
                    handler.overstyrt(ansattId,data.brukerId)
                    refresh(ansattId,data)
                }
            }.getOrElse {
                when (it) {
                    is RegelException ->  throw RegelException(it,OVERSTYRING_MESSAGE_CODE,arrayOf(it.regel.metadata.kortNavn,ansattId.verdi,data.brukerId.verdi)).also {
                        handler.avvist(ansattId,data.brukerId)
                    }
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
     fun refresh(ansattId: AnsattId, data: OverstyringData)  = Unit.also {
        log.info("Refresh cache overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.mask()}'")
    }
}

@Component
@Counted
class OverstyringHandler {
    private val log = getLogger(OverstyringHandler::class.java)
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

