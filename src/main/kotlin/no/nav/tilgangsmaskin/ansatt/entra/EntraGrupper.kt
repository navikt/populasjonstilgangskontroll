package no.nav.tilgangsmaskin.ansatt.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.util.*
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String = "N/A") {
    override fun equals(other: Any?) = other is EntraGruppe && id == other.id
    override fun hashCode() = id.hashCode()

}

fun Set<EntraGruppe>.girNasjonalTilgang() = any { it.id == NASJONAL.id }

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupper(@JsonProperty("@odata.nextLink") val next: URI? = null,
                        val value: Set<EntraGruppe> = emptySet())
