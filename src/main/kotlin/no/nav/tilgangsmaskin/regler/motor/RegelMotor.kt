package no.nav.tilgangsmaskin.regler.motor

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.avvist
import no.nav.tilgangsmaskin.regler.motor.BulkResultat.Companion.ok
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.TELLENDE_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.stereotype.Component

@Component
class RegelMotor(
    @param:Qualifier(KJERNE) private val kjerne: RegelSett,
    @param:Qualifier(KOMPLETT) private val komplett: RegelSett,

    private val cfg: RegelConfig,
    private val logger: RegelMotorLogger) {

    @WithSpan
    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, komplett)

    @WithSpan
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, kjerne)

    @WithSpan
    private fun evaluer(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) {
        regelSett.regler.forEach { regel ->
            logger.evaluerer(ansatt, bruker, regel)
            if (!cfg.isEnabled(regel.navn)) {
                logger.trace("Regel ${regel.navn} er deaktivert i konfigurasjonen, hopper over evaluering.")
                return@forEach
            }
            if (!regel.evaluer(ansatt, bruker)) {
                logger.avvist(ansatt, bruker, regelSett, regel)
                throw RegelException(ansatt, bruker, regel)
            }
        }
        logger.ok(ansatt, bruker,regelSett)
    }

    @WithSpan
    fun bulkRegler(ansatt: Ansatt, brukere: Set<BrukerOgRegelsett>) =
        buildSet {
            val n = brukere.size
            brukere.forEachIndexed { index, (bruker, type) ->
                val resultat = runCatching {
                    logger.trace("Bulk evaluerer #$index/$n: ${bruker.oppslagId.maskFnr()}")
                    evaluer(ansatt, bruker, type.regelSett())
                    ok(bruker)
                }.getOrElse {
                    if (it is RegelException) {
                        avvist(bruker, it)
                    } else throw it
                }
                add(resultat)
            }
        }.also {
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

data class BulkResultat(val bruker: Bruker,val status: HttpStatus, val regel: Regel? = null) {
    companion object {
        fun ok(bruker: Bruker) = BulkResultat( bruker,NO_CONTENT)
        fun avvist(bruker: Bruker,e: RegelException) = BulkResultat( bruker,FORBIDDEN, e.regel)
    }
}
