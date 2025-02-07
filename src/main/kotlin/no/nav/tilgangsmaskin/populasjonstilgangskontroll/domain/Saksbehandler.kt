package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import java.util.UUID

class Saksbehandler(val  attributter: SaksbehandlerAttributter, val grupper: List<EntraGruppe>) {
    data class SaksbehandlerAttributter(val id: UUID, val navId: NavId, val fornavn: String, val etternavn: String, val enhetsNummer: Enhetsnummer)

    fun kanBehandle(gruppe: FortroligGruppe) = grupper.any { it.id == gruppe.gruppeId }

    override fun toString() = "${javaClass.simpleName} [attributter=$attributter,grupper=$grupper]"

}