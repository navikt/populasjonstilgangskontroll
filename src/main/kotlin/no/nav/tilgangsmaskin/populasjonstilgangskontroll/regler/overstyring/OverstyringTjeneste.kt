package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
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


    fun erOverstyrt(ansattId: NavId, brukerId: Fødselsnummer): Boolean {
        val nyeste = adapter.finnNyeste(ansattId.verdi, brukerId.verdi) ?: return false
        val now = Instant.now()
        return if (nyeste.expires.isBefore(now)) {
            false.also {
                log.warn("Overstyring har gått ut på tid for ${now.diffFrom(nyeste.expires)} siden for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
            }
        } else {
            true.also {
                log.warn("Overstyring er gyldig i ${nyeste.expires.diffFrom(now)} til for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
            }
        }
    }

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer, metadata: OverstyringMetadata)  =
         runCatching {
                log.info("Eksekverer kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(brukerId))
                adapter.lagre(ansattId.verdi, brukerId.verdi, metadata)
                refresh(ansattId, brukerId, metadata)
                log.info("Overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' oppdatert i cache")
            }.getOrElse {
                when (it) {
                    is RegelException -> throw it.withDetail("Kjerneregel ${it.regel.beskrivelse.kortNavn} er ikke overstyrbar, kunne ikke overstyre tilgang for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
    private fun refresh(ansattId: NavId, brukerId: Fødselsnummer, metadata: OverstyringMetadata)  = Unit
}

