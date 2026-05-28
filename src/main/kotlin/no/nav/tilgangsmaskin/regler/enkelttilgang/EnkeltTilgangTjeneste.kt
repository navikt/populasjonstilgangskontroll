package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.regler.motor.OverstyringTeller
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.INGEN_REGEL_TAG
import no.nav.tilgangsmaskin.regler.motor.Regel.Companion.regelTag
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.OVERSTYRING_MESSAGE_CODE
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangClientValidator.OverstyringException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Component
@Transactional(readOnly = true)
@Timed
class EnkeltTilgangTjeneste(
    private val ansattTjeneste: AnsattTjeneste,
    private val brukerTjeneste: BrukerTjeneste,
    private val adapter: EnkeltTilgangJPAAdapter,
    private val motor: RegelMotor,
    private val proxy: EntraProxyTjeneste,
    private val validator: KonsumentValidator,
    private val teller: OverstyringTeller) {

    private val log = getLogger(javaClass)

    fun tilganger(ansattId: AnsattId, brukerIds: List<BrukerId>) =
        adapter.gjeldendeTilganger(ansattId.verdi, brukerIds.map { it.verdi })

    fun harEnkeltTilgang(ansattId: AnsattId, brukerId: BrukerId) =
        gjeldendeEnkeltTilgang(ansattId, brukerId)
            ?.also {
                log.trace("Overstyring er gyldig i {} til for {} og {}", it.diffFromNow(), ansattId, brukerId)
            } != null


    @Transactional
    fun overstyr(ansattId: AnsattId, data: EnkeltTilgangData): Boolean {
        try {
            validator.valider()
            val ansatt = ansattTjeneste.ansatt(ansattId)
            val bruker = brukerTjeneste.brukerMedNærmesteFamilie(data.brukerId.verdi)
            motor.kjerneregler(ansatt, bruker)
            adapter.overstyr(ansattId.verdi, enhetFor(ansattId), data)
            teller.tell(INGEN_REGEL_TAG, OVERSTYRT)
            log.info("Overstyring til og med ${data.gyldigtil} ble registrert for $ansattId og ${data.brukerId}")
            return true
        } catch (e: RegelException) {
            log.warn("Overstyring er avvist av kjerneregler for $ansattId og ${data.brukerId}", e)
            teller.tell(regelTag(e.regel), IKKE_OVERSTYRT, tokenSystemTag(UTILGJENGELIG))
            throw RegelException(
                OVERSTYRING_MESSAGE_CODE,
                arrayOf(e.regel.kortNavn, ansattId.verdi, data.brukerId.verdi),
                e = e
            )
        } catch (e: OverstyringException) {
            log.warn("Overstyring feilet pga klientvalidering ${e.message} for $ansattId og ${data.brukerId}", e)
            teller.tell(INGEN_REGEL_TAG, IKKE_OVERSTYRT, tokenSystemTag(e.system))
            throw e
        }
    }

    private fun gjeldendeEnkeltTilgang(ansattId: AnsattId, brukerId: BrukerId): Instant? =
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