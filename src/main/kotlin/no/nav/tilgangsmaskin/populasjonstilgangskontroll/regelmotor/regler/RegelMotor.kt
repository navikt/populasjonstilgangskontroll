package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelType.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(vararg regler: Regel)  {
    private val log = LoggerFactory.getLogger(javaClass)

    val kjerneRegelSett = RegelSett(KJERNE to regler.filterIsInstance<KjerneRegel>().sortedWith(INSTANCE))
    val komplettRegelSett = RegelSett(KOMPLETT to regler.sortedWith(INSTANCE))

    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = sjekk(ansatt, bruker, komplettRegelSett)
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = sjekk(ansatt, bruker, kjerneRegelSett)

    fun sjekk(ansatt: Ansatt, bruker: Bruker, type: RegelType) =
        sjekk(ansatt, bruker, type.regelSett())

    private fun sjekk(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) =
        with(regelSett) {
            log.info("Sjekker ${type.tekst} for '${ansatt.ansattId.verdi}' og '${bruker.brukerId.verdi}'")
            regler.forEachIndexed { index, regel ->
                log.info("[${index.plus(1)}/${regelSett.size}] Sjekker regel: '${regel.metadata.kortNavn}' fra '$tekst' for '${ansatt.ansattId.verdi}/${ansatt.bruker?.brukerId?.verdi}'og '${bruker.brukerId.verdi}'")
                if (!regel.test(ansatt,bruker)) {
                    throw RegelException(bruker.brukerId, ansatt.ansattId, regel).also {
                        log.warn("[${index.plus(1)}/${regelSett.size}] Tilgang avvist av regel '${regel.metadata.kortNavn}' i '$tekst' (${regel.metadata.begrunnelse.årsak})")
                    }
                }
            }.also {
                log.info("${type.tekst} ga tilgang OK for '${ansatt.ansattId.verdi}' og '${bruker.brukerId.verdi}'")
            }
    }
    private fun RegelType.regelSett() =
        when(this) {
            KJERNE -> kjerneRegelSett
            KOMPLETT -> komplettRegelSett
        }
}
