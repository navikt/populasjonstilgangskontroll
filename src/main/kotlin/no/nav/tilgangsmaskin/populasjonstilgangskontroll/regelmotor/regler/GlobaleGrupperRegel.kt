package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import java.util.*

abstract class GlobaleGrupperRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String):
    KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.kreverGlobalGruppe(gruppe))  {
            ansatt.kanBehandle(id)
        } else true
    override val metadata = Regel.RegelBeskrivelse(kortNavn, gruppe.begrunnelse)
}