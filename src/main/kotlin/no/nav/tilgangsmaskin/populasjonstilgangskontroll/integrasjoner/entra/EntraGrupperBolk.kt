package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolk(
    @com.fasterxml.jackson.annotation.JsonProperty("@odata.nextLink") val next: java.net.URI? = null,
    val value: List<EntraGruppe> = emptyList())   {
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    data class EntraGruppe(val id: java.util.UUID, val displayName: String)
}