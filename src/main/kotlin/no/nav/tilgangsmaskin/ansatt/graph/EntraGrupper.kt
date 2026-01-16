package no.nav.tilgangsmaskin.ansatt.graph

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import java.net.URI
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String = UTILGJENGELIG) {
    override fun equals(other: Any?) = other is EntraGruppe && id == other.id
    override fun hashCode() = id.hashCode()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupper(@param:JsonProperty("@odata.nextLink") val next: URI? = null,
                        val value: Set<EntraGruppe> = emptySet())
