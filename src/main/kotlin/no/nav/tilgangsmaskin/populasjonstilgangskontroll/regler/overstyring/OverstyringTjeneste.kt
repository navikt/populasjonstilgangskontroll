package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.Overstyring.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.format
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.withDetail
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.compareTo
import kotlin.rem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toKotlinDuration

@Component
@Cacheable(OVERSTYRING)
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste,private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)


    fun erOverstyrt(ansattId: NavId, brukerId: Fødselsnummer) =
        nyesteOverstyring(ansattId, brukerId)?.let {
            val now = Instant.now()
            val isOverstyrt = it.expires?.isAfter(now) == true
            if (!isOverstyrt) {
                val utgått = java.time.Duration.between(it.expires,now).toKotlinDuration().format()
                log.warn("Overstyring har gått ut på tid for $utgått siden for id=${ansattId.verdi} and brukerId=${brukerId.mask()}")
            }
            isOverstyrt
        } == true

    fun nyesteOverstyring(id: NavId, brukerId: Fødselsnummer) =
        adapter.nyesteOverstyring(id.verdi, brukerId.verdi)

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration = 5.minutes) : Any =
         runCatching {
                log.info("Eksekverer kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(brukerId))
                adapter.lagre(ansattId.verdi, brukerId.verdi, varighet)
                refresh(ansattId, brukerId, varighet)
                log.info("Overstyring for '${ansattId.verdi}' og ${brukerId.mask()} oppdatert i cache")
            }.getOrElse {
                when (it) {
                    is RegelException -> throw it.withDetail("Kjerneregel ${it.regel.beskrivelse.kortNavn} er ikke overstyrbar, kunne ikke overstyre tilgang for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
    private fun refresh(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration) : Any = Unit


}

