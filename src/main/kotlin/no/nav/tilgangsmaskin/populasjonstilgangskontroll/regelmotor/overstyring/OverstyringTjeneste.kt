package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelBeskrivelse.Companion.OVERSTYRING_MESSAGE_CODE
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
class OverstyringTjeneste(private val ansatte: AnsattTjeneste, private val brukere: BrukerTjeneste, private val adapter: OverstyringJPAAdapter, private val motor: RegelMotor) {

    private val log = getLogger(javaClass)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        with(adapter.gjeldendeOverstyring(ansattId.verdi, brukerId.verdi, brukere.bruker(brukerId).historiskeIdentifikatorer.map { it.verdi })) {
            when {
                this == null -> false.also {
                    log.trace("Ingen overstyring for $ansattId og $brukerId ble funnet i databasen")
                }

                isBeforeNow() -> false.also {
                    log.trace("Overstyring har gått ut på tid for ${diffFromNow()} siden for $ansattId og $brukerId")
                }

                else -> true.also {
                    log.trace("Overstyring er gyldig i ${diffFromNow()} til for $ansattId og $brukerId")
                }
            }
        }

    fun overstyr(ansattId: AnsattId, data: OverstyringData)  =
         runCatching {
                log.info("Sjekker kjerneregler før eventuell overstyring for $ansattId og ${data.brukerId}")
                motor.kjerneregler(ansatte.ansatt(ansattId), brukere.bruker(data.brukerId))
                adapter.overstyr(ansattId.verdi, data).also {
                    log.info("Overstyring er registrert for $ansattId og ${data.brukerId}")
                    refresh(ansattId, data)
                }
            }.getOrElse {
                when (it) {
                    is RegelException ->  throw RegelException(OVERSTYRING_MESSAGE_CODE, arrayOf(it.regel.kortNavn,ansattId.verdi,data.brukerId.verdi), it).also {
                        log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId}")
                    }
                    else -> throw it.also {
                        log.error("Ukjent feil ved overstyring for $ansattId", it)
                    }
                }
         }

    @CachePut(OVERSTYRING)
    fun refresh(ansattId: AnsattId, data: OverstyringData)  =
        Unit.also {
            log.info("Refresh cache overstyring for $ansattId og ${data.brukerId}")
        }

    fun sjekk(ansattId: AnsattId, e: Throwable) =
        when (e) {
            is BulkRegelException -> sjekkOverstyringer(ansattId, e)
            is RegelException -> sjekkOverstyring(ansattId, e)
            else -> throw e.also {
                log.error("Ukjent feil ved tilgangskontroll for $ansattId", it)
            }
        }

    private fun sjekkOverstyring(ansattId: AnsattId, e: RegelException) =
        with(e.regel) {
            log.trace("Sjekker om regler er overstyrt for $ansattId og ${e.brukerId}")
            if (erOverstyrbar) {
                if (erOverstyrt(ansattId, e.brukerId)) {
                    log.info("Overstyrt tilgang er gitt til $ansattId og ${e.brukerId}")
                } else {
                    throw e.also {
                        log.warn("Ingen overstyring, avvisning fra regel '$kortNavn' og $ansattId og ${e.brukerId} opprettholdes")
                    }
                }
            } else {
                throw e.also {
                    log.trace("Avvisning fra kjerneregel $kortNavn for $ansattId og ${e.brukerId} opprettholdes")
                }
            }
        }

    private fun sjekkOverstyringer(ansattId: AnsattId, e: BulkRegelException) {
        with(e.exceptions.toMutableList()) {
            removeIf {
                runCatching {
                    sjekkOverstyring(ansattId, it)
                }.isSuccess
            }
            if (isNotEmpty()) {
                throw BulkRegelException(ansattId, this).also {
                    log.warn(it.message)
                }
            }
        }
    }
}
