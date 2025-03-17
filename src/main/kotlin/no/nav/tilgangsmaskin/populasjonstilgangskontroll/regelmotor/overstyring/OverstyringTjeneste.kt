package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattOperasjoner
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.diffFrom
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@Cacheable(OVERSTYRING)
@Transactional
@Timed
class OverstyringTjeneste(private val ansatt: AnsattOperasjoner, private val bruker: BrukerTjeneste, private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId): Boolean {
        val brukerIds = bruker.bruker(brukerId).historiskeIdentifikatorer.map { it.verdi }
        val gjeldendeDato = adapter.gjeldendeOverstyringGyldighetDato(ansattId.verdi, brukerId.verdi, brukerIds)
        return if (gjeldendeDato != null) {
            val now = Instant.now()
            if (gjeldendeDato.isBefore(now)) {
                log.warn("Overstyring har gått ut på tid for ${now.diffFrom(gjeldendeDato)} siden for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                false
            } else {
                log.trace("Overstyring er gyldig i ${gjeldendeDato.diffFrom(now)} til for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                true
            }
        } else {
            log.trace("Ingen overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' ble funnet i databasen")
            false
        }
    }

    fun overstyr(ansattId: AnsattId,data: OverstyringData)  =
         runCatching {
                log.info("Sjekker kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.mask()}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(data.brukerId))
                adapter.overstyr(ansattId.verdi, data)
                refresh(ansattId,data)
                log.info("Overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.mask()}' oppdatert i cache")
            }.getOrElse {
                when (it) {
                    is RegelException ->  throw RegelException(it,OVERSTYRING_MESSAGE_CODE,arrayOf(it.regel.metadata.kortNavn,ansattId.verdi,data.brukerId.verdi))
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
     fun refresh(ansattId: AnsattId, data: OverstyringData)  = Unit.also {
        log.info("Refresh cache overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.mask()}'")
    }
}

