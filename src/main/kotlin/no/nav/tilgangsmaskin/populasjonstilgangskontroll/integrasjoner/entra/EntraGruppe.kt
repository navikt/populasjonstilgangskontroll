package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: java.util.UUID, val displayName: String)