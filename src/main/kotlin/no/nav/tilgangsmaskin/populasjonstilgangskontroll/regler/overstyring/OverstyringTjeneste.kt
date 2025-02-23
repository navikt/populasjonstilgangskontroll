package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.diffFrom
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.withDetail
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@Cacheable(OVERSTYRING)
@Transactional
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste,private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId): Boolean {
        val nyeste = adapter.finnGjeldendeOverstyringDato(ansattId.verdi, brukerId.verdi)
        return if (nyeste != null) {
            val now = Instant.now()
            if (nyeste.isBefore(now)) {
                log.warn("Overstyring har gått ut på tid for ${now.diffFrom(nyeste)} siden for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                false
            } else {
                log.trace("Overstyring er gyldig i ${nyeste.diffFrom(now)} til for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                true
            }
        } else {
            log.trace("Ingen overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' ble funnet i databasen")
            false
        }
    }

    fun overstyr(ansattId: AnsattId, brukerId: BrukerId, metadata: OverstyringMetadata)  =
         runCatching {
                log.info("Sjekker kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(brukerId))
                adapter.overstyr(ansattId.verdi, brukerId.verdi, metadata)
                refresh(ansattId, brukerId, metadata)
                log.info("Overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' oppdatert i cache")
            }.getOrElse {
                when (it) {
                    is RegelException -> throw it.withDetail("Kjerneregel ${it.regel.beskrivelse.kortNavn} er ikke overstyrbar, kunne ikke overstyre tilgang for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
    private fun refresh(ansattId: AnsattId, brukerId: BrukerId, metadata: OverstyringMetadata)  = Unit
}

