package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.core.Ordered.LOWEST_PRECEDENCE


@Component
@Order(LOWEST_PRECEDENCE)
class DummyRegel() : Regel {
    override fun test(k: Kandidat, s: Saksbehandler): Boolean = true
    override val forklaring = RegelForklaring("Dummy","Alltid true", "00")
}