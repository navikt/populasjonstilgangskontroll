package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
    @JsonProperty("personident")
    val brukerId: String?,
    @JsonProperty("navident")
    val ansattId: String?,
    val startdato: LocalDate,
    val sluttdato: LocalDate?
)