package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.*
import no.nav.tilgangsmaskin.tilgang.RegelConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component

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


    fun bulkRegler(ansatt: Ansatt, brukere: Set<BrukerOgType>) =
        brukere.map { (bruker, type) ->
            runCatching {
                evaluer(ansatt, bruker, type.regelSett())
                Triple(bruker.brukerId, NO_CONTENT, null)
            }.getOrElse {
                if (it is RegelException) {
                    Triple(bruker.brukerId, FORBIDDEN, it.regel)
                } else {
                    Triple(bruker.brukerId, INTERNAL_SERVER_ERROR, null)
                }
            }
        }.toSet()


    private fun RegelType.regelSett() =
        when (this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
            OVERSTYRBAR_REGELTYPE -> overstyrbar
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

}
