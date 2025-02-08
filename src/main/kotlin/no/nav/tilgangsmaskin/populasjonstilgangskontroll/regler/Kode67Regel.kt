package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(HIGHEST_PRECEDENCE)
class Kode67Regel : Regel {


    override fun test(k: Kandidat, s: Saksbehandler) =
        when  {
            k.kreverGruppe(FORTROLIG) -> s.kanBehandle(FORTROLIG)
            k.kreverGruppe(STRENGT_FORTROLIG) -> s.kanBehandle(STRENGT_FORTROLIG)
            else -> true
        }

    override val forklaring: RegelForklaring
        get() = RegelForklaring("Beskyttelsesregler","Saksbehandler har ikke tilgang", "67", false)
}