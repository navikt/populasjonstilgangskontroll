package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import org.springframework.stereotype.Component

@Component

class DefaultRegelMotor(private vararg val regler: Regel) : RegelMotor {

    override fun vurderTilgang(k: Kandidat, s: Saksbehandler) =
        regler.sortedWith(AnnotationAwareOrderComparator.INSTANCE).forEach {
            if (!it.test(k, s)) {
                throw TilgangException(k.ident, s.navId, it.forklaring)
            }
        }
    }


