package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import java.util.UUID

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : Regel {
    override fun test(k: Kandidat, s: Saksbehandler) = if (k.kreverGruppe(GlobalGruppe.EGEN))  {
        s.kanBehandle(id)
    } else true
    override val beskrivelse = RegelBeskrivelse("Egen ansatt", "007")
}