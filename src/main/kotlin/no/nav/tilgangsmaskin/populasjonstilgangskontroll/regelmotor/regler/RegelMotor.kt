package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.KJERNE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.Companion.OVERSTYRBAR
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class RegelMotor(@Qualifier(KJERNE) private val kjerne: RegelSett, @Qualifier(OVERSTYRBAR) private val overstyrbar: RegelSett, private val handler: RegelsettResultatHandler = RegelsettResultatHandler())  {
    private val log = LoggerFactory.getLogger(javaClass)

    private val komplett = RegelSett(KOMPLETT_REGELTYPE to kjerne.regler + overstyrbar.regler)


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
                handler.ok(type, ansatt.ansattId, bruker.brukerId)
            }
    }

    fun bulkRegler(ansatt: Ansatt, brukere: List<Pair<Bruker, RegelType>>) {
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
        when(this) {
            KJERNE_REGELTYPE -> kjerne
            KOMPLETT_REGELTYPE -> komplett
            OVERSTYRBAR_REGELTYPE -> overstyrbar
        }

    override fun toString() = "${javaClass.simpleName} [kjerneregler=$kjerne,kompletteregler=$komplett]"

    @Component
    @Counted
    class RegelsettResultatHandler {
        private val log = LoggerFactory.getLogger(javaClass)
        fun avvist(pos: String, ansattId: AnsattId, brukerId: BrukerId, regel: Regel) {
            log.warn("[#$pos] Tilgang avvist av regel '${regel.metadata.kortNavn}' (${regel.metadata.begrunnelse.årsak})")
        }
        fun ok(type: RegelType, ansattId: AnsattId, brukerId: BrukerId) {
            log.info("${type.beskrivelse.replaceFirstChar { it.uppercaseChar() }} ga tilgang OK for '$ansattId' og '${brukerId.mask()}'")
        }
    }
}
