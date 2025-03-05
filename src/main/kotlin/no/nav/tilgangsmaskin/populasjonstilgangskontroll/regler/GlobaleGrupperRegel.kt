package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import java.util.UUID

abstract class GlobaleGrupperRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): KjerneRegel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.kreverGlobalGruppe(gruppe))  {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse(kortNavn, gruppe.begrunnelse)
}

interface KjerneRegel : Regel