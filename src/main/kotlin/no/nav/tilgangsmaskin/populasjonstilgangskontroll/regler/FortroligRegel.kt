package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel: Regel {
    override val forklaring = RegelForklaring("Kode 7", "Saksbehandler har ikke tilgang til kode 7", "7")
    override fun test(k: Kandidat, s: Saksbehandler) =
        if (k.kreverGruppe(FORTROLIG)) {
            s.kanBehandle(FORTROLIG)
        }
        else true
}