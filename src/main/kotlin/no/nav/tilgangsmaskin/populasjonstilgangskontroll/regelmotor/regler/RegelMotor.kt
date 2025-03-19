package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KOMPLETT
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KJERNE_REGELTYPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KOMPLETT_REGELTYPE
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class RegelMotor(@Qualifier(KJERNE) val kjerne: RegelSett, @Qualifier(KOMPLETT) private val komplett: RegelSett, private val handler: RegelAvvisningsHandler = RegelAvvisningsHandler())  {
    private val log = LoggerFactory.getLogger(javaClass)

    @Counted
    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = sjekkRegler(ansatt, bruker, komplett)
    @Counted
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = sjekkRegler(ansatt, bruker, kjerne)

    fun sjekkRegler(ansatt: Ansatt, bruker: Bruker, type: RegelType) =
        sjekkRegler(ansatt, bruker, type.regelSett())

    private fun sjekkRegler(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) =
        with(regelSett) {
            log.info("Sjekker ${type.beskrivelse} for '${ansatt.ansattId.verdi}' og '${bruker.brukerId}'")
            regler.forEachIndexed { index, regel ->
                log.trace("[${index.plus(1)}/${regelSett.size}] Sjekker regel: '${regel.metadata.kortNavn}' fra $beskrivelse for '${ansatt.ansattId.verdi}/${ansatt.bruker?.brukerId}'og '${bruker.brukerId}'")
                if (!regel.test(ansatt,bruker)) {
                    throw RegelException(bruker.brukerId, ansatt.ansattId, regel).also {
                        handler.avvist("${index.plus(1)}/${regelSett.size}", ansatt.ansattId, bruker.brukerId, regel)
                    }
                }
            }.also {
                log.info("${type.beskrivelse.replaceFirstChar { it.uppercaseChar() }} ga tilgang OK for '${ansatt.ansattId.verdi}' og '${bruker.brukerId}'")
            }
    }
    private fun RegelType.regelSett() =
        when(this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

    @Component
    @Counted
    class RegelAvvisningsHandler() {
        private val log = LoggerFactory.getLogger(javaClass)
        fun avvist(pos: String,ansattId: AnsattId, brukerId: BrukerId, regel: Regel) {
            log.warn("[#$pos] Tilgang avvist av regel '${regel.metadata.kortNavn}' (${regel.metadata.begrunnelse.årsak})")
        }
    }
}
