package no.nav.tilgangsmaskin.regler.overstyring

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBeforeNow
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
@Timed
class OverstyringTjeneste(
        private val ansatte: AnsattTjeneste,
        private val brukere: BrukerTjeneste,
        private val adapter: OverstyringJPAAdapter,
        private val motor: RegelMotor,
        private val teller: OverstyringTeller) {

    private val log = getLogger(javaClass)

    @Transactional(readOnly = true)
    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId): Boolean {
        val overstyring = adapter.gjeldendeOverstyring(
                ansattId.verdi, brukerId.verdi,
                brukere.brukerMedNærmesteFamilie(brukerId.verdi).historiskeIds.map { it.verdi })

        return when {
            overstyring == null -> {
                log.trace("Ingen overstyring for {} og {} ble funnet", ansattId, brukerId)
                false
            }
            overstyring.isBeforeNow() -> {
                log.trace("Overstyring har gått ut på tid for {} siden for {} og {}", overstyring.diffFromNow(), ansattId, brukerId)
                false
            }
            else -> {
                log.trace("Overstyring er gyldig i {} til for {} og {}", overstyring.diffFromNow(), ansattId, brukerId)
                true
            }
        }
    }

    fun overstyr(ansattId: AnsattId, data: OverstyringData) =
        runCatching {
            log.info("Sjekker kjerneregler før eventuell overstyring for $ansattId og ${data.brukerId}")
            motor.kjerneregler(ansatte.ansatt(ansattId), brukere.brukerMedNærmesteFamilie(data.brukerId.verdi))
            adapter.overstyr(ansattId.verdi, data).also {
                teller.tell(Tags.of("overstyrt", true.toString()))
                log.info("Overstyring er utført for $ansattId og ${data.brukerId}")
            }
        }.getOrElse {
            when (it) {
                is RegelException -> throw RegelException(
                        OVERSTYRING_MESSAGE_CODE,
                        arrayOf(it.regel.kortNavn, ansattId.verdi, data.brukerId.verdi),
                        it).also {
                    log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId}")
                    teller.tell(Tags.of("kortnavn", it.regel.kortNavn, "overstyrt", false.toString()))
                }
                else -> throw it
            }
        }
}