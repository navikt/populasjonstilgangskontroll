package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.RegelBeskrivelse
import java.util.*

abstract class GlobalGruppeRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.kreverGlobalGruppe(gruppe))  {
            ansatt.kanBehandle(id)
        } else true
    override val metadata = RegelBeskrivelse(kortNavn, gruppe.begrunnelse)
}