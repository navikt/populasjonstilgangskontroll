package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(private vararg val regler: Regel)  {
    private val log = LoggerFactory.getLogger(javaClass)

     fun vurderTilgang(k: Bruker, s: Ansatt) =
        regler.sortedWith(INSTANCE).forEach {
            log.info(CONFIDENTIAL,"Eksekverer regel: ${it.beskrivelse.navn}")
            if (!it.test(k, s)) {
                throw RegelException(k.ident, s.navId, it)
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
Geogrfisk tilgang: (Mangler datasettene for dette)
 **/