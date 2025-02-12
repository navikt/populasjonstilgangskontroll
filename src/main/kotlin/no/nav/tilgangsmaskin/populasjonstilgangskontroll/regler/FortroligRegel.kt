package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): Regel {
    override val beskrivelse = RegelBeskrivelse("Kode 7", "7")
    override fun test(k: Kandidat, s: Saksbehandler) =
        if (k.kreverGruppe(FORTROLIG_GRUPPE)) {
            s.kanBehandle(id)
        }
        else true
}