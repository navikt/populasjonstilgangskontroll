package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.EntraGrupperBolk
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.FortroligeGrupper.STRENGT_FORTROLIG
import java.util.UUID

class Saksbehandler(val ident: NavId, uuid: UUID, val tilganger: List<EntraGrupperBolk.EntraGruppe>) {
    val kanBehandleStrengtFortrolig = tilganger.any { it.gruppeNavn == STRENGT_FORTROLIG.gruppeNavn }

}