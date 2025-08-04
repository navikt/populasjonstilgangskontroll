package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(GlobalGruppe.UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUkjentBosted && !(ansatt erMedlemAv GlobalGruppe.UKJENT_BOSTED) }
}