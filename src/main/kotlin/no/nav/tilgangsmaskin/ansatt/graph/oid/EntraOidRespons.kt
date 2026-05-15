package no.nav.tilgangsmaskin.ansatt.graph.oid

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraOidRespons(@param:JsonProperty("value") val oids: Set<EntraOid>) {
    data class EntraOid(val id: UUID)
}