package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class NorgeRegel : GlobalGruppeRegel(GlobalGruppe.NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { !(ansatt erMedlemAv GlobalGruppe.NASJONAL) && !(ansatt kanBehandle bruker.geografiskTilknytning) }
}