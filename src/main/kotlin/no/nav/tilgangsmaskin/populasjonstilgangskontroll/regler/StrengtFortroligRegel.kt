package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID): Regel {
    override val beskrivelse = RegelBeskrivelse("Kode 6", "6")
    override fun test(k: Kandidat, s: Saksbehandler) =
        if (k.kreverGruppe(STRENGT_FORTROLIG)) {
            s.kanBehandle(id)
        } else true
}
