package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String)
