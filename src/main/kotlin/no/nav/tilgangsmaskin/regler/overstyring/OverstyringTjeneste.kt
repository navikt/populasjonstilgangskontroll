package no.nav.tilgangsmaskin.regler.overstyring

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBeforeNow
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.regelTag
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import org.jboss.logging.MDC
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Component
@Transactional(readOnly = true)
@Timed
class OverstyringTjeneste(
    private val ansattTjeneste: AnsattTjeneste,
    private val brukerTjeneste: BrukerTjeneste,
    private val adapter: OverstyringJPAAdapter,
    private val motor: RegelMotor,
    private val proxy: EntraProxyTjeneste,
    private val teller: OverstyringTeller) {

    private val log = getLogger(javaClass)

    fun overstyringer(ansattId: AnsattId, brukerIds: List<BrukerId>) =
        adapter.gjeldendeOverstyringer(ansattId.verdi,brukerIds.map { it.verdi }).associate { it.first to it.second }.filter {
            erOverstyrt(ansattId,it.key,it.value)
        }
            .map { it.key }

    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId): Boolean {
        log.info("Sjekker eventuell overstyring for $ansattId og ${brukerId.verdi.maskFnr()}")
        val overstyring = adapter.gjeldendeOverstyring(
            ansattId.verdi, brukerId.verdi,
            brukerTjeneste.brukerMedNærmesteFamilie(brukerId.verdi).historiskeIds.map { it.verdi })?.expires
        return erOverstyrt(ansattId,brukerId,overstyring)
    }
    @Transactional
    fun overstyr(ansattId: AnsattId, data: OverstyringData) =
        runCatching {
            MDC.put(USER_ID, ansattId.verdi)
            log.info("Sjekker kjerneregler før eventuell overstyring for $ansattId og ${data.brukerId}")
            motor.kjerneregler(ansattTjeneste.ansatt(ansattId), brukerTjeneste.brukerMedNærmesteFamilie(data.brukerId.verdi))
            adapter.overstyr(ansattId.verdi, enhetFor(ansattId), data).also {
                teller.tell(INGEN_REGEL,OVERSTYRT)
                log.info("Overstyring til og med ${data.gyldigtil} ble registrert for $ansattId og ${data.brukerId}")
            }
            true
        }.getOrElse {
            overstyringFeilet(it, ansattId, data)
        }

    private fun overstyringFeilet(t: Throwable,ansattId: AnsattId, data: OverstyringData): Nothing {
        when (t) {
            is RegelException -> throw RegelException(OVERSTYRING_MESSAGE_CODE,
                arrayOf(t.regel.kortNavn, ansattId.verdi, data.brukerId.verdi),
                e = t).also {
                log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId}", it)
                teller.tell(regelTag(t.regel),IKKE_OVERSTYRT)
            }
            else -> throw t
        }
    }

    private fun enhetFor(ansattId: AnsattId) =
        runCatching {
            proxy.enhet(ansattId).enhetnummer.verdi
        }.getOrDefault(UTILGJENGELIG)

    private fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId, expires: Instant?) =
        when {
            expires == null -> {
                log.trace("Ingen overstyring for {} og {} ble funnet", ansattId, brukerId)
                false
            }
            expires.isBeforeNow() -> {
                log.trace("Overstyring har gått ut på tid for {} siden for {} og {}", expires.diffFromNow(), ansattId, brukerId)
                false
            }
            else -> {
                log.trace("Overstyring er gyldig i {} til for {} og {}", expires.diffFromNow(), ansattId, brukerId)
                true
            }
        }

    companion object {
        private const val TAG = "overstyrt"
        private val OVERSTYRT = Tag.of(TAG, "true")
        private val IKKE_OVERSTYRT = Tag.of(TAG, "false")
    }
}