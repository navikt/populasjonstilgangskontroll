package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import java.util.UUID

abstract class KjerneRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.kreverGlobalGruppe(gruppe))  {
            ansatt.kanBehandle(id)
        } else true

    override val beskrivelse = RegelBeskrivelse(kortNavn, gruppe.begrunnelse)
}