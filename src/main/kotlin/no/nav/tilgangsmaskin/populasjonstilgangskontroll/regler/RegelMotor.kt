package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelType.*
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
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

    fun kompletteRegler(ansatt: Ansatt, bruker: Bruker) = sjekk(ansatt, bruker, KOMPLETT)
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = sjekk(ansatt, bruker, KJERNE)

    fun sjekk(ansatt: Ansatt, bruker: Bruker, type: RegelType) =
        sjekk(ansatt, bruker, type.regelSett())

    private fun sjekk(ansatt: Ansatt, bruker: Bruker, regelSett: RegelSett) =
        with(regelSett) {
            regler.forEachIndexed { index, regel ->
                log.info(CONFIDENTIAL,"[$index] Sjekker regel: '${regel.metadata.kortNavn}' fra regelsett '$tekst' for '${ansatt.ansattId.verdi}' og '${bruker.brukerId.verdi}'")
                if (!regel.test(ansatt,bruker)) {
                    throw RegelException(bruker.brukerId, ansatt.ansattId, regel).also {
                        log.warn("Tilgang avvist av regel ${index.plus(1)} (${regel.metadata.kortNavn}) i regelsett '${tekst}' (${regel.metadata.begrunnelse.årsak})")
                    }
                }
            }.also {
                log.info("${regler.size} ${type.tekst} Tilgang OK for '${ansatt.ansattId.verdi}' og '${bruker.brukerId.verdi}'")
            }
    }
    private fun RegelType.regelSett() =
        when(this) {
            KJERNE -> kjerneRegelSett
            KOMPLETT -> komplettRegelSett
        }
}


/** Flyt for GEO-sjekk
 * (Kan overstyres av system)
 *
 * Ansatt har gruppe GE_GEO-NASJONAL
 * Bruker har ikke registert GT --> Ansatte med GEO_Udefinert skal ha tilgang til personer uten GT/Udefnert GT
 * Bruker har GT = landskode --> Sjekk mot ansattes gruper -GEO-Utland
 * Brukers GT stemmer med en av Ansattes liste over ENHET-GT _
 * Brukers oppfølgingskontornr stemmer med en av Ansattes liste over ENHET-KONTORNR _(datasett må komme fra POAO (sansynligbvis)) team OBO
 *
 *
 * Avvik på tilganger som må dekkes:
 * Bruker har kun kommunetilhørighet i GT, mens kommunene har bydelsoppdeling --> Diskusjon om ansatte i kommunene Oslo(0301, Bergen(4601), Trondheim(5001) og Stavanger(1103) skal ha tilgang til kommunenummeret i tilleggg til bydelen
 *
 *
 */