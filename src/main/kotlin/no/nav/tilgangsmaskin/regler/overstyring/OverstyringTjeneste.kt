package no.nav.tilgangsmaskin.regler.overstyring

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBeforeNow
import no.nav.tilgangsmaskin.regler.motor.BulkRegelException
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringEntity.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Cacheable(OVERSTYRING)
@Transactional
@Timed
class OverstyringTjeneste(
        private val ansatte: AnsattTjeneste,
        private val brukere: BrukerTjeneste,
        private val adapter: OverstyringJPAAdapter,
        private val motor: RegelMotor,
        private val registry: MeterRegistry,
        private val token: Token) {

    private val log = getLogger(javaClass)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        with(
                adapter.gjeldendeOverstyring(
                        ansattId.verdi,
                        brukerId.verdi,
                        brukere.nærmesteFamilie(brukerId.verdi).historiskeIds.map { it.verdi })) {
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

    fun overstyr(ansattId: AnsattId, data: OverstyringData) =
        runCatching {
            log.info("Sjekker kjerneregler før eventuell overstyring for $ansattId og ${data.brukerId}")
            motor.kjerneregler(ansatte.ansatt(ansattId), brukere.nærmesteFamilie(data.brukerId.verdi))
            adapter.overstyr(ansattId.verdi, data).also {
                log.info("Overstyring er registrert for $ansattId og ${data.brukerId}")
                refresh(ansattId, data)
            }
        }.getOrElse {
            when (it) {
                is RegelException -> throw RegelException(
                        OVERSTYRING_MESSAGE_CODE,
                        arrayOf(it.regel.kortNavn, ansattId.verdi, data.brukerId.verdi),
                        it).also {
                    log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId}")
                }

                else -> throw it.also {
                    log.error("Ukjent feil ved overstyring for $ansattId", it)
                }
            }
        }

    @CachePut(OVERSTYRING)
    fun refresh(ansattId: AnsattId, data: OverstyringData) =
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
        if (e.regel.erOverstyrbar && erOverstyrt(ansattId, e.bruker.brukerId)) {
            tellOverstyring()
        } else {
            throw e.also {
                tellAvslag(e.regel.kortNavn, token.systemNavn)
            }
        }

    private fun sjekkOverstyringer(ansattId: AnsattId, e: BulkRegelException) {
        val remainingExceptions = e.exceptions.filterNot {
            runCatching { sjekkOverstyring(ansattId, it) }.isSuccess
        }
        if (remainingExceptions.isNotEmpty()) {
            throw BulkRegelException(ansattId, remainingExceptions)
        }
    }

    private fun tellAvslag(kortNavn: String, systemNavn: String) {
        Counter.builder("regel.avslag.total")
            .description("Antall avslag pr regel")
            .tag("kortnavn", kortNavn)
            .tag("system", systemNavn)
            .register(registry).increment()
    }

    private fun tellOverstyring() {
        Counter.builder("regel.overstyring.total")
            .description("Antall overstyringer")
            .register(registry).increment()
    }
}


