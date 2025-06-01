package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.boot.conditionals.ConditionalOnDev
import org.springframework.boot.context.properties.ConfigurationProperties
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelMotor.BulkRegelResult.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@Service
@Timed
class RegelTjeneste(
    private val motor: RegelMotor,
    private val brukere: BrukerTjeneste,
    private val ansatte: AnsattTjeneste,
    private val overstyring: OverstyringTjeneste) {
    private val log = getLogger(javaClass)

    fun kompletteRegler(ansattId: AnsattId, brukerId: String) {
        val elapsedTime = measureTime {
            log.info("Sjekker ${KOMPLETT_REGELTYPE.beskrivelse} for $ansattId og ${brukerId.maskFnr()}")
            val bruker = brukere.medNærmesteFamilie(brukerId)
            runCatching {
                motor.kompletteRegler(ansatte.ansatt(ansattId), bruker)
            }.getOrElse {
                if (overstyring.erOverstyrt(ansattId,bruker.brukerId)) {
                    Unit
                }
                else throw it
            }
        }
        log.info("Tid brukt på komplett regelsett for $ansattId og ${brukerId.maskFnr()}: ${elapsedTime.inWholeMilliseconds}ms")
    }

    fun kjerneregler(ansattId: AnsattId, brukerId: String) =
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.medUtvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): List<Pair<BrukerId, Any>> {
        val resultater = motor.bulkRegler(ansatte.ansatt(ansattId), idOgType.brukerIdOgType()).map {
            when (it) {
                is Success -> it.brukerId to "OK"
                is RegelFailure -> if (overstyring.erOverstyrt(ansattId, it.brukerId))
                    it.brukerId to "OK"
                      else it.brukerId to "403"

                is InternalError -> it.brukerId to "500"
            }
        }
        val notFound = idOgType.map { it.brukerId }
            .filterNot { brukerId -> resultater.any { it.first.verdi == brukerId } }
            .map { BrukerId(it) to "404" }
        return resultater + notFound
    }

    private fun Set<IdOgType>.brukerIdOgType() =
        mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId]?.let { bruker ->
                bruker to spec.type
            }
        }.toSet()
}

@ConfigurationProperties("regler")
data class RegelConfig(val toggles: Map<String,Boolean>) {

    fun isEnabled(type: String) = toggles[type] ?: true

}


