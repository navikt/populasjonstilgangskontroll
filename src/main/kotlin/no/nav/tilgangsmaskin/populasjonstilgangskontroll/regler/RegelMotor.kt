package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelSpec.RegelType.*
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(vararg regler: Regel)  {
    private val log = LoggerFactory.getLogger(javaClass)

    val kjerneregler = RegelSett(KJERNE,regler.sortedWith(INSTANCE).filterIsInstance<KjerneRegel>())
    val alleRegler = RegelSett(ALLE,regler.sortedWith(INSTANCE))

    fun alleRegler(ansatt: Ansatt, bruker: Bruker) = eksekver(ansatt, bruker, alleRegler)
    fun kjerneregler(ansatt: Ansatt, bruker: Bruker) = eksekver(ansatt, bruker, kjerneregler)

    private fun eksekver(ansatt: Ansatt, bruker: Bruker, regelsett: RegelSett) {
        log.trace("Eksekverer regelett {} for ansatt '{}' og bruker '{}'", regelsett.type, ansatt.ansattId, bruker.brukerId)
        regelsett.regler.forEach {
            log.info(CONFIDENTIAL,"Eksekverer regel: '${it.metadata.kortNavn}' for ansatt '${ansatt.ansattId}' og bruker '${bruker.brukerId}'")
            if (!it.test(ansatt,bruker)) {
                throw RegelException(bruker.brukerId, ansatt.ansattId, it).also {
                    log.warn("Tilgang avvist av regel '${it.regel.metadata.kortNavn}'")
                }
            }
        }.also {
            log.info("Regelsett ${regelsett.type
            } OK for ansatt '${ansatt.ansattId}' og bruker '${bruker.brukerId}'")
        }
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