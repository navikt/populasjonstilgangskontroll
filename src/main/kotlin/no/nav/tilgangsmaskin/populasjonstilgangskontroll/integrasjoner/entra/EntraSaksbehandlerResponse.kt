package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import com.fasterxml.jackson.annotation.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Enhetsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraSaksbehandlerResponse(@JsonProperty("value") val attributter: List<MSGraphSaksbehandlerAttributter>)  {
    data class MSGraphSaksbehandlerAttributter(val id: UUID)
}