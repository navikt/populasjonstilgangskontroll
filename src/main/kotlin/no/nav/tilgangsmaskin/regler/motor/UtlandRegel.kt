package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUtenlandskBosted && !(ansatt erMedlemAv UTENLANDSK) }
}







