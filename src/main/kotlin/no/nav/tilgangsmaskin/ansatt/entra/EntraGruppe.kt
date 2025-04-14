package no.nav.tilgangsmaskin.ansatt.entra

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppe(val id: UUID, val displayName: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGruppeNullable(val id: UUID, val displayName: String? = null)

