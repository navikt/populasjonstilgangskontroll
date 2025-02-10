package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(private vararg val regler: Regel)  {

     fun vurderTilgang(k: Kandidat, s: Saksbehandler) =
        regler.sortedWith(INSTANCE).forEach {
            println("Executing rule: ${it.forklaring.navn}")
            if (!it.test(k, s)) {
                throw RegelException(k.ident, s.navId, it.forklaring)
            }
        }
    }
