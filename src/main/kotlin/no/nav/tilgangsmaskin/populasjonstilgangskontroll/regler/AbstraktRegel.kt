package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import java.util.UUID

abstract class AbstraktRegel(private val gruppe: GlobalGruppe, private val id: UUID, private val kode: String): Regel {
    override fun test(k: Kandidat, s: Saksbehandler) = if (k.kreverGruppe(gruppe))  {
        s.kanBehandle(id)
    } else true

    override val beskrivelse = RegelBeskrivelse(javaClass.simpleName.regelNavn(),kode)


    private fun String.regelNavn(): String {
        return replace(Regex("([A-Z][a-z]*)$"), "").trim()
    }
}