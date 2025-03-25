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
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.maskFnr
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
                log.info("Sjekker kjerneregler før eventuell overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.verdi}'")
                motor.kjerneregler(ansatt.ansatt(ansattId), bruker.bruker(data.brukerId))
                adapter.overstyr(ansattId.verdi, data).also {
                    handler.overstyrt(ansattId,data.brukerId)
                    refresh(ansattId,data)
                }
            }.getOrElse {
                when (it) {
                    is RegelException ->  throw RegelException(it,OVERSTYRING_MESSAGE_CODE,arrayOf(it.regel.kortNavn,ansattId.verdi,data.brukerId.verdi)).also {
                        handler.avvist(ansattId,data.brukerId)
                    }
                    else -> throw it
                }
         }

    @CachePut(OVERSTYRING)
     fun refresh(ansattId: AnsattId, data: OverstyringData)  = Unit.also {
        log.info("Refresh cache overstyring for ansatt '${ansattId.verdi}' og bruker '${data.brukerId.maskFnr()}'")
    }

    fun sjekk(ansattId: AnsattId, e: Throwable) =
        when (e) {
            is BulkRegelException -> sjekkOverstyringer(e, ansattId)
            is RegelException -> sjekkOverstyring(e, ansattId)
            else -> throw e.also { log.error("Ukjent feil ved tilgangskontroll for '${ansattId.verdi}", it) }
        }

    private fun sjekkOverstyring(e: RegelException, ansattId: AnsattId) =
        with(e.regel) {
            log.trace("Sjekker om regler er overstyrt for  $ansattId og ${e.brukerId}")
            if (erOverstyrbar) {
                if (erOverstyrt(ansattId, e.brukerId)) {
                    log.info("Overstyrt tilgang er gitt til $ansattId og ${e.brukerId}")
                } else {
                    throw e.also { log.warn("Ingen overstyring, tilgang avvist av regel $kortNavn og $ansattId og ${e.brukerId} opprettholdes") }
                }
            } else {
                throw e.also { log.trace("Tilgang avvist av kjerneregel $kortNavn for $ansattId og ${e.brukerId}, avvisning opprettholdes") }
            }
        }

    private fun sjekkOverstyringer(e: BulkRegelException, ansattId: AnsattId) {
        with(e.exceptions.toMutableList()) {
            removeIf {
                runCatching { sjekkOverstyring(it, ansattId) }.isSuccess
            }.also {
                if (it) {
                    log.info("Ignorerer ${e.exceptions.size - size} avvisninger grunnet overstyring for $ansattId")
                } else {
                    log.info("Ingen overstyringer ble funnet for $ansattId og ${e.exceptions.map { it.brukerId }}")
                }
            }
            if (isNotEmpty()) {
                throw BulkRegelException(ansattId, this).also {
                    val avvist = intersect(e.exceptions).map { it.brukerId to it.regel.avvisningKode}
                    log.error("Følgende identer ble avvist for $ansattId : $avvist")
                }
            }
        }
    }
}
