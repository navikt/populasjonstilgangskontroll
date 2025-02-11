package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import java.util.*

class Saksbehandler(val  attributter: SaksbehandlerAttributter,vararg val grupper: EntraGruppe) {
    data class SaksbehandlerAttributter(val id: UUID, val navId: NavId, val fornavn: String, val etternavn: String, val enhetsNummer: Enhetsnummer)

    val navId = attributter.navId
    fun kanBehandle(id: UUID) = grupper.any { it.id == id }

    override fun toString() = "${javaClass.simpleName} [attributter=$attributter,grupper=${grupper.contentToString()}]"

}