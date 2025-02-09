package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel: Regel {
    override val forklaring = RegelForklaring("Kode 6","Saksbehandler har ikke tilgang til kode 6", "6")
    override fun test(k: Kandidat, s: Saksbehandler) =
        if (k.kreverGruppe(STRENGT_FORTROLIG)) {
            s.kanBehandle(STRENGT_FORTROLIG)
        } else true
}
