package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE
import org.springframework.stereotype.Component

@Component
class RegelMotor(private vararg val regler: Regel)  {
    private val log = LoggerFactory.getLogger(javaClass)

     fun vurderTilgang(k: Kandidat, s: Saksbehandler) =
        regler.sortedWith(INSTANCE).forEach {
            log.trace("Eksekverer regel: ${it.beskrivelse.navn}")
            if (!it.test(k, s)) {
                throw RegelException(k.ident, s.navId, it)
            }
        }
    }
