package no.nav.tilgangsmaskin.regler.motor

import net.minidev.json.annotate.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelMotor.BulkRegelResult.*
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus.*

@Component
class RegelMotor(
    @Qualifier(KJERNE) private val kjerne: RegelSett,
    @Qualifier(OVERSTYRBAR) private val overstyrbar: RegelSett,
    private val cfg: RegelConfig,
    private val logger: RegelMotorLogger) {

    private val komplett = RegelSett(KOMPLETT_REGELTYPE to kjerne.regler + overstyrbar.regler)


    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, komplett)

    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = evaluer(ansatt, bruker, kjerne)

    private fun evaluer(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) {
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


    fun bulkRegler(ansatt: Ansatt, brukere: Set<Pair<Bruker, RegelType>>) =
        brukere.map { (bruker, type) ->
            logger.info("Bulk evaluerer ${type.beskrivelse} for ${bruker.brukerId}")
            runCatching {
                evaluer(ansatt, bruker, type.regelSett())
                Success(bruker.brukerId).also {
                    logger.info("Bulk OK for ${bruker.brukerId} med ${type.beskrivelse}")
                }
            }.getOrElse {
                if (it is RegelException) {
                    RegelFailure(bruker.brukerId, it, HttpStatus.valueOf(it.statusCode.value()).also {
                        logger.info("Bulk Avvist  $it for ${bruker.brukerId}")
                    })
                } else {
                    InternalError(bruker.brukerId, INTERNAL_SERVER_ERROR, it)
                }
            }
        }.toSet()


    sealed class BulkRegelResult(val statusCode: HttpStatus, val brukerId: BrukerId) {
        class Success( brukerId: BrukerId) : BulkRegelResult(ACCEPTED, brukerId)
        class RegelFailure( brukerId: BrukerId, @JsonIgnore val exception: RegelException, val status: HttpStatus) : BulkRegelResult(status, brukerId)
        class InternalError(brukerId: BrukerId,val status: HttpStatus, @JsonIgnore val exception: Throwable) : BulkRegelResult(status,brukerId)
    }

    private fun RegelType.regelSett() =
        when (this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
            OVERSTYRBAR_REGELTYPE -> overstyrbar
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

}
