package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringEntity.Companion.OVERSTYRING
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
class OverstyringTjeneste(private val ansattTjeneste: AnsattTjeneste, private val brukerTjeneste: BrukerTjeneste,private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId): Boolean {
        val gjeldendeDato = adapter.gjeldendeOverstyringGyldighetDato(ansattId.verdi, brukerId.verdi)
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
                motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.bruker(data.brukerId))
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

