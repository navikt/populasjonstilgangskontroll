package no.nav.tilgangsmaskin.tilgang

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import kotlin.time.measureTime
import  no.nav.tilgangsmaskin.tilgang.BulkResultater.BulkResultat

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

    fun bulkRegler(ansattId: AnsattId, idOgType: Set<IdOgType>): BulkResultater {
        val ansatt = ansatte.ansatt(ansattId)
        val brukere = idOgType.brukerIdOgType()
        val resultater = motor.bulkRegler(ansatt, brukere)

        val godkjente = resultater
            .filter { it.second == NO_CONTENT }
            .map {
                BulkResultat(it.first, NO_CONTENT)
            }.toSet()

        val avviste = resultater
            .filter { it.second == UNAUTHORIZED && !overstyring.erOverstyrt(ansattId, it.first) }
            .map { avvist ->
                val bruker = brukere.first { it.first.brukerId == avvist.first }.first
                val e = RegelException(ansatt, bruker, avvist.third!!, status = avvist.second)
                BulkResultat(e.bruker.brukerId, e.status, e.body)
            }.toSet()

        val funnetBrukerIder = resultater.map { it.first }.toSet()
        val ikkeFunnet = idOgType
            .map { it.brukerId }
            .filterNot { it in funnetBrukerIder }
            .map { BulkResultat(it, NOT_FOUND) }
            .toSet()

        return BulkResultater(ansattId, godkjente + avviste + ikkeFunnet)
    }
    private fun Set<IdOgType>.brukerIdOgType() =
        mapNotNull { spec ->
            brukere.brukere(*map { it.brukerId.verdi }.toTypedArray())
                .associateBy { it.brukerId.verdi }[spec.brukerId.verdi]?.let { bruker ->
                bruker to spec.type
            }
        }.toSet()
}