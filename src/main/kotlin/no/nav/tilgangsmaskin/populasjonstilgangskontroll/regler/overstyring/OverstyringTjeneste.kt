package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.Overstyring.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Component
@Cacheable(OVERSTYRING)
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste,private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(OverstyringTjeneste::class.java)


    fun erOverstyrt(id: NavId, brukerId: Fødselsnummer) =
        nyesteOverstyring(id, brukerId)?.let { it.expires?.isAfter(Instant.now())  } == true

    fun nyesteOverstyring(id: NavId, brukerId: Fødselsnummer) =
        adapter.nyesteOverstyring(id.verdi, brukerId.verdi)

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration = 5.minutes) : Any =
         runCatching {
                log.info("Kjører alle reglene for eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                motor.alleRegler(ansatt.ansatt(ansattId), bruker.bruker(brukerId))
                adapter.lagre(ansattId.verdi, brukerId.verdi, varighet).also {
                    refresh(ansattId, brukerId, varighet).also {
                        log.info("Overstyring for '${ansattId.verdi}' og ${brukerId.mask()} oppdatert i cache")
                    }
                }
            }.getOrElse {
                when (it) {
                    is RegelException -> {
                        log.info("Regelkjøring feilet, sjekker om overstyring er mulig for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                        if (it.regel.erOverstyrbar) {
                            log.info("${it.regel.beskrivelse.kortNavn} er overstyrbar, gir tilgang til ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                            adapter.lagre(ansattId.verdi, brukerId.verdi, varighet).also {
                                log.info("Overstyring for '${ansattId.verdi}' og ${brukerId.mask()} lagret")
                                refresh(ansattId, brukerId, varighet)
                                log.info("Overstyring for '${ansattId.verdi}' og ${brukerId.mask()} oppdatert i cache")
                            }
                        }
                        else {
                            throw it.also {
                                log.error("${it.regel.beskrivelse.kortNavn} er ikke overstyrbar, kunne ikke overstyre tilgang for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'", it)
                            }
                        }
                    }
                    else -> throw it.also {
                        log.error("Ukjent feil ved forsøk på overstyring for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'", it)
                    }
                }
         }

    @CachePut(OVERSTYRING)
    private fun refresh(ansattId: NavId, brukerId: Fødselsnummer, varighet: Duration) : Any = Unit

}

