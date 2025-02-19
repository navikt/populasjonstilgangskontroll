package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import java.util.UUID

abstract class KjerneRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): Regel {
    override fun test(bruker: Bruker, s: Ansatt) =
        if (bruker.kreverGruppe(gruppe))  {
            s.kanBehandle(id)
        } else true

    override val beskrivelse = Regel.RegelBeskrivelse(kortNavn, gruppe.begrunnelse, false)
}