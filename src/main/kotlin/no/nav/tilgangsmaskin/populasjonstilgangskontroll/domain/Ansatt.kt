package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import java.util.*

class Ansatt(val  attributter: AnsattAttributter, vararg val grupper: EntraGruppe) {
    data class AnsattAttributter(val id: UUID, val navId: NavId, val navn: Navn, val enhetsNummer: Enhetsnummer)

    val navId = attributter.navId
    fun kanBehandle(id: UUID) = grupper.any { it.id == id }

    override fun toString() = "${javaClass.simpleName} [attributter=$attributter,grupper=${grupper.contentToString()}]"

}