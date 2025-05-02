package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.OVERSTYRBAR_REGELTYPE
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class RegelMotor(
        @Qualifier(KJERNE) private val kjerne: RegelSett,
        @Qualifier(OVERSTYRBAR) private val overstyrbar: RegelSett,
        private val handler: RegelMotorResultatLogger = RegelMotorResultatLogger()) {
    private val log = getLogger(javaClass)

    private val komplett = RegelSett(KOMPLETT_REGELTYPE to kjerne.regler + overstyrbar.regler)


    @Counted
    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = sjekkRegler(ansatt, bruker, komplett)

    @Counted
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = sjekkRegler(ansatt, bruker, kjerne)

    fun sjekkRegler(ansatt: Ansatt, bruker: Bruker, type: RegelType) =
        sjekkRegler(ansatt, bruker, type.regelSett())

    private fun sjekkRegler(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) {
        regelSett.regler.forEachIndexed { index, regel ->
            log.trace("[${index + 1}/${regelSett.size}] Sjekker regel: '${regel.kortNavn}' fra ${regelSett.beskrivelse} ${ansatt.ansattId} og ${ansatt.bruker?.brukerId} og ${bruker.brukerId}")
            if (!regel.evaluer(ansatt, bruker)) {
                handler.avvist("${index + 1}/${regelSett.size}", ansatt.ansattId, bruker.brukerId, regel)
                throw RegelException(bruker.brukerId, ansatt.ansattId, regel)
            }
        }
        handler.ok(regelSett.type, ansatt.ansattId)
    }


    fun bulkRegler(ansatt: Ansatt, brukere: Set<Pair<Bruker, RegelType>>) {
        val avvisninger = brukere.mapNotNull { (bruker, type) ->
            runCatching { sjekkRegler(ansatt, bruker, type) }
                .exceptionOrNull()
                ?.takeIf { it is RegelException } as? RegelException
        }
        if (avvisninger.isNotEmpty()) {
            throw BulkRegelException(ansatt.ansattId, avvisninger)
        }
    }

    private fun RegelType.regelSett() =
        when (this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
            OVERSTYRBAR_REGELTYPE -> overstyrbar
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

}
