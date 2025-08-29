package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.Bulk.Companion.avvist
import no.nav.tilgangsmaskin.regler.motor.Bulk.Companion.ok
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.*
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import io.opentelemetry.instrumentation.annotations.WithSpan

@Component
class RegelMotor(
    @Qualifier(KJERNE) private val kjerne: RegelSett,
    @Qualifier(KOMPLETT) private val komplett: RegelSett,

    private val cfg: RegelConfig,
    private val logger: RegelMotorLogger) {

    @WithSpan
    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, komplett)

    @WithSpan
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, kjerne)

    @WithSpan
    private fun evaluer(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) {
        logger.tellRegelSett(regelSett)
        regelSett.regler.forEach { regel ->
            logger.evaluerer(ansatt, bruker, regel)
            if (!cfg.isEnabled(regel.navn)) {
                logger.trace("Regel ${regel.navn} er deaktivert i konfigurasjonen, hopper over evaluering.")
                return@forEach
            }
            if (!regel.evaluer(ansatt, bruker)) {
                logger.avvist(ansatt, bruker, regel)
                throw RegelException(ansatt, bruker, regel)
            }
        }
        logger.ok(ansatt, bruker,regelSett)
    }

    @WithSpan("regelMotor.bulk")
    fun bulkRegler(ansatt: Ansatt, brukere: Set<BrukerOgRegelsett>) =
        (brukere.map { (bruker, type) ->
            runCatching {
                evaluer(ansatt, bruker, type.regelSett())
                ok(bruker)
            }.getOrElse {
                if (it is RegelException) {
                    avvist(bruker, it)
                } else throw it
            }
        }.toSet()).also {
            logger.tellBulkSize(it.size)
        }


    private fun RegelType.regelSett() =
        when (this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
            OVERSTYRBAR_REGELTYPE -> komplett.regler.filter { it is OverstyrbarRegel }.let { RegelSett(OVERSTYRBAR_REGELTYPE to it) }
            TELLENDE_REGELTYPE -> komplett.regler.filter { it is TellendeRegel }.let { RegelSett(TELLENDE_REGELTYPE to it) }
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

}

data class Bulk(val brukerId: BrukerId, val status: HttpStatus, val regel: Regel? = null) {
    companion object {
        fun ok(bruker: Bruker) = Bulk(bruker.brukerId, NO_CONTENT)
        fun avvist(bruker: Bruker, e: RegelException) = Bulk(bruker.brukerId, FORBIDDEN, e.regel)

    }
}
