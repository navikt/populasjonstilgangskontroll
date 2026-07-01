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
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelMotorLogger.Companion.INGEN_REGEL_TAG
import no.nav.tilgangsmaskin.regler.motor.RegelMotorLogger.Companion.tag
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Component
@Transactional(readOnly = true)
@Timed
class EnkeltTilgangTjeneste(
    private val ansattTjeneste: AnsattTjeneste,
    private val bruker: BrukerTjeneste,
    private val adapter: EnkeltTilgangJPAAdapter,
    private val motor: RegelMotor,
    private val proxy: EntraProxyTjeneste,
    private val teller: EnkeltTilgangTeller) {

    private val log = getLogger(javaClass)

    fun tilganger(ansattId: AnsattId, brukerIds: Set<BrukerId>) =
        adapter.gjeldendeTilganger(ansattId.verdi, brukerIds.map { it.verdi }.toSet())

    fun harTilgang(ansattId: AnsattId, brukerId: BrukerId) =
        gjeldendeEnkeltTilgang(ansattId, brukerId)
            ?.also {
                log.trace("Enkelttilgang er gyldig i {} til for {} og {}", it.diffFromNow(), ansattId, brukerId)
            } != null


    @Transactional
    fun registrerTilgang(ansattId: AnsattId, data: EnkeltTilgangData) =
        runCatching {
            motor.kjerneregler(ansattTjeneste.ansatt(ansattId),
                bruker.medNærmesteFamilie(data.brukerId.verdi))
            adapter.enkeltTilgang(ansattId.verdi, enhetFor(ansattId), data)
            teller.tell(INGEN_REGEL_TAG, ENKELTTILGANG_GITT)
            log.info("Enkelttilgang til og med ${data.gyldigtil} ble registrert for $ansattId og ${data.brukerId}")
            true
        }.onFailure { e ->
            when (e) {
                is RegelException -> {
                    log.warn("Enkelttilgang er avvist av kjerneregler for $ansattId og ${data.brukerId}", e)
                    teller.tell(e.regel.tag(), ENKELTTILGANG_AVVIST)
                }
            }
        }.getOrThrow()

    private fun gjeldendeEnkeltTilgang(ansattId: AnsattId, brukerId: BrukerId): Instant? =
        adapter.gjeldendeTilgang(ansattId.verdi, brukerId.verdi,
            bruker.medNærmesteFamilie(brukerId.verdi).historiskeIds.map {
                it.verdi
            })?.expires

    private fun enhetFor(ansattId: AnsattId) =
        runCatching {
            proxy.enhet(ansattId).enhetnummer.verdi
        }.getOrDefault(UTILGJENGELIG)


    private companion object {
        private const val TAG = "overstyrt"
        private val ENKELTTILGANG_GITT = Tag.of(TAG, "true")
        private val ENKELTTILGANG_AVVIST = Tag.of(TAG, "false")
    }
}