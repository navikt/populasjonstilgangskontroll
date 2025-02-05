package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGrupperBolk
import java.util.UUID

class Saksbehandler(val  attributter: SaksbehandlerAttributter, val gruoper: List<EntraGrupperBolk.EntraGruppe>) {
    data class SaksbehandlerAttributter(val id: UUID, val navId: NavId, val fornavn: String, val etternavn: String, val enhetsNummer: Enhetsnummer)

    fun kanBehandle(gruppe: FortroligGruppe) =  gruoper.any { it.gruppeNavn == gruppe.gruppeNavn }
}