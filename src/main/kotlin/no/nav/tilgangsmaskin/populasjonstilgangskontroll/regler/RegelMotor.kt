package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(private vararg val regler: Regel)  {
    private val log = LoggerFactory.getLogger(javaClass)

     fun vurderTilgang(bruker: Bruker, ansatt:  Ansatt) =
        regler.sortedWith(INSTANCE).forEach {
            log.info(CONFIDENTIAL,"Eksekverer regel: ${it.beskrivelse.kortNavn}")
            if (!it.test(bruker, ansatt)) {
                throw RegelException(bruker.ident, ansatt.navId, it)
            }
        }
    }
/**
Prioritert utslagskriterier:
Harde regler:
Kode 6: Strengt fortrolig adresse
Kode 19: trengt fortrolig adresse utland
Kode 7 : Fortrolig adresse
Egen ansatt: Skjerming
Familie: (mangler datasettene for dette)
Verge: (ikkje implementert og mangler datasettene for dette)
Oppslag på egen person :(mangler datasettene for dette) (hovuddel vil håndters via skjerming, men avskjermede ansatte dekkes ikkje av skjerming)
Overstyrbare regler:
Geogrfisk tilgang: (Flyt for gGEO-tilgang spesifisert under
 **/

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