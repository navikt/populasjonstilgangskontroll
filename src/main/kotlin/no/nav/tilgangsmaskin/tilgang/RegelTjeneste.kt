package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import org.springframework.boot.context.properties.ConfigurationProperties
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
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
            val bruker = brukere.brukerMedNærmesteFamilie(brukerId)
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
        motor.kjerneregler(ansatte.ansatt(ansattId), brukere.brukerMedUtvidetFamilie(brukerId))

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): Set<Pair<BrukerId, HttpStatus>> {
        log.info("Sjekker bulk for ansatt $ansattId og $idOgType brukere")
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerIdOgType()
        val resultater = motor.bulkRegler(ansatt, brukere).map { (brukerId, status, regel) ->
            if (status == UNAUTHORIZED && overstyring.erOverstyrt(ansattId, brukerId)) {
                brukerId to ACCEPTED
            } else {
                val bruker = brukere.first { it.first.brukerId == brukerId }.first
               // RegelException(ansatt, bruker,regel!!,  status = status)
                brukerId to status
            }
        }.toSet()
        val resultBrukerIds = resultater.map { it.first.verdi }.toSet()
        val notFound = (idOgType.map { it.brukerId }.toSet() - resultBrukerIds)
        val nf = notFound.map { BrukerId(it) to NOT_FOUND }
        return resultater + nf
    }
    private fun Set<IdOgType>.brukerIdOgType(): Set<Pair<Bruker, RegelSett.RegelType>> {
        log.info("Bulk henter ${size} brukere")
        return mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId]?.let { bruker ->
                bruker to spec.type
            }
        }.toSet().also { log.info("Henter ${size} brukere $it") }
    }
}

@ConfigurationProperties("regler")
data class RegelConfig(val toggles: Map<String,Boolean> = emptyMap()) {
    fun isEnabled(regel: String) = toggles[regel.lowercase() +".enabled"]  ?: true
}


