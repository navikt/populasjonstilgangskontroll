package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraSaksbehandlerResponse(@JsonProperty("value") val attributter: List<MSGraphSaksbehandlerAttributter>)  {
    data class MSGraphSaksbehandlerAttributter(
        val id: java.util.UUID,
        val onPremisesSamAccountName: NavId,
        val displayName: String,
        val givenName: String,
        val surname: String,
        val streetAddress: Enhetsnummer
    )
}