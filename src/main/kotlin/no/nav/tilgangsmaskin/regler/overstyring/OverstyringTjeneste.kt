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
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.INGEN_REGEL_TAG
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.regelTag
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringClientValidator.OverstyringException
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
    private val validator: OverstyringClientValidator,
    private val teller: OverstyringTeller) {

    private val log = getLogger(javaClass)

    fun overstyringer(ansattId: AnsattId, brukerIds: List<BrukerId>) =
        adapter.gjeldendeOverstyringer(ansattId.verdi, brukerIds.map { it.verdi })

    fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) =
        gjeldendeOverstyring(ansattId, brukerId)
            ?.also {
                log.trace("Overstyring er gyldig i {} til for {} og {}", it.diffFromNow(), ansattId, brukerId.maskert())
            } != null


    @Transactional
    fun overstyr(ansattId: AnsattId, data: OverstyringData) =
        runCatching {
            MDC.put(USER_ID, ansattId.verdi)
            validator.validerKonsument()
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val bruker = brukerTjeneste.brukerMedNærmesteFamilie(data.brukerId.verdi)
            motor.kjerneregler(ansatt,bruker)
            val enhet = enhetFor(ansattId)
            adapter.overstyr(ansattId.verdi, enhet, data).also {
                teller.tell(INGEN_REGEL_TAG,OVERSTYRT)
                log.info("Overstyring til og med ${data.gyldigtil} ble registrert for $ansattId og ${data.brukerId.maskert()}")
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
                log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId.maskert()}", it)
                teller.tell(regelTag(t.regel),IKKE_OVERSTYRT,tokenSystemTag(UTILGJENGELIG))
            }
            is OverstyringException -> throw t.also {
                log.warn("Overstyring feilet pga klientvalidering ${t.message} for $ansattId og ${data.brukerId.maskert()}", it)
                teller.tell(INGEN_REGEL_TAG,IKKE_OVERSTYRT,tokenSystemTag(t.system))
            }
            else -> throw t
        }
    }
    private fun gjeldendeOverstyring(ansattId: AnsattId, brukerId: BrukerId): Instant? =
        adapter.gjeldendeOverstyring(ansattId.verdi, brukerId.verdi,
        brukerTjeneste.brukerMedNærmesteFamilie(brukerId.verdi).historiskeIds.map {
            it.verdi
        })?.expires

    private fun enhetFor(ansattId: AnsattId) =
        runCatching {
            proxy.enhet(ansattId).enhetnummer.verdi
        }.getOrDefault(UTILGJENGELIG)


    companion object {
        private fun tokenSystemTag(system: String) = Tag.of("system",system)
        private const val TAG = "overstyrt"
        private val OVERSTYRT = Tag.of(TAG, "true")
        private val IKKE_OVERSTYRT = Tag.of(TAG, "false")
    }
}