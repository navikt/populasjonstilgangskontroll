package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.AzureObjectId

interface MicrosoftGraphClient {
    fun hentAdGrupperForNavAnsatt(navAnsattAzureId: AzureObjectId): List<AdGruppe>
}
data class AdGruppe(
    val id: AzureObjectId,
    val name: String
)