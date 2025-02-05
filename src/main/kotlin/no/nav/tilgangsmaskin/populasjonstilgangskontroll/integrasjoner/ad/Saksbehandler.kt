package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.FortroligeGrupper
import java.util.UUID

class Saksbehandler(val  attributter: SaksbehandlerAttributter, val gruoper: List<EntraGruppe>) {
    data class SaksbehandlerAttributter(val id: UUID, val navId: NavId, val fornavn: String, val etternavn: String, val enhetsNummer: Enhetsnummer)

    fun kanBehandle(gruppe: FortroligeGrupper) =  gruoper.any { it.gruppeNavn == gruppe.gruppeNavn }
}

