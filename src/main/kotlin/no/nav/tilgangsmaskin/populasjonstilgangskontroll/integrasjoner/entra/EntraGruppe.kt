package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: java.util.UUID, val displayName: String)