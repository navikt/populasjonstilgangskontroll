package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelMotor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.TimeExtensions.isBeforeNow
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Cacheable(OVERSTYRING)
@Transactional
@Timed
class OverstyringTjeneste(private val ansatt: AnsattTjeneste, private val bruker: BrukerTjeneste, private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor, private val handler: OverstyringResultatHandler = OverstyringResultatHandler()) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        with(adapter.gjeldendeOverstyring(ansattId.verdi, brukerId.verdi, bruker.bruker(brukerId).historiskeIdentifikatorer.map { it.verdi })) {
            when {
                this == null -> handler.ingen(ansattId, brukerId)
                isBeforeNow() -> handler.utgått(ansattId, brukerId, diffFromNow())
                else -> handler.gyldig(ansattId, brukerId, diffFromNow())
            }
        }

    fun overstyr(ansattId: AnsattId, data: OverstyringData)  =
         runCatching {
                log.info("Sjekker kjerneregler før eventuell overstyring for ansatt $ansattId og bruker ${data.brukerId}")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(data.brukerId))
                adapter.overstyr(ansattId.verdi, data).also {
                    handler.overstyrt(ansattId,data.brukerId)
                    refresh(ansattId,data)
                }
            }.getOrElse {
                when (it) {
                    is RegelException ->  throw RegelException(OVERSTYRING_MESSAGE_CODE, arrayOf(it.regel.kortNavn,ansattId.verdi,data.brukerId.verdi), it).also {
                        handler.avvist(ansattId,data.brukerId)
                    }
                    else -> throw it.also { log.error("Ukjent feil ved overstyring for $ansattId", it) }
                }
         }

    @CachePut(OVERSTYRING)
    fun refresh(ansattId: AnsattId, data: OverstyringData)  =
        Unit.also {
            log.info("Refresh cache overstyring for $ansattId og bruker ${data.brukerId}")
        }

    fun sjekk(ansattId: AnsattId, e: Throwable) =
        when (e) {
            is BulkRegelException -> sjekkOverstyringer(ansattId, e)
            is RegelException -> sjekkOverstyring(ansattId, e)
            else -> throw e.also { log.error("Ukjent feil ved tilgangskontroll for $ansattId", it) }
        }

    private fun sjekkOverstyring(ansattId: AnsattId, e: RegelException) =
        with(e.regel) {
            log.trace("Sjekker om regler er overstyrt for $ansattId og ${e.brukerId}")
            if (erOverstyrbar) {
                if (erOverstyrt(ansattId, e.brukerId)) {
                    log.info("Overstyrt tilgang er gitt til $ansattId og ${e.brukerId}")
                } else {
                    throw e.also { log.warn("Ingen overstyring, tilgang avvist av regel '$kortNavn' og $ansattId og ${e.brukerId} opprettholdes") }
                }
            } else {
                throw e.also { log.trace("Tilgang avvist av kjerneregel $kortNavn for $ansattId og ${e.brukerId}, avvisning opprettholdes") }
            }
        }

    private fun sjekkOverstyringer(ansattId: AnsattId, e: BulkRegelException) {
        with(e.exceptions.toMutableList()) {
            removeIf {
                runCatching { sjekkOverstyring(ansattId, it) }.isSuccess
            }
            if (isNotEmpty()) {
                throw BulkRegelException(ansattId, this).also {
                    log.error(it.message)
                }
            }
        }
    }
}
