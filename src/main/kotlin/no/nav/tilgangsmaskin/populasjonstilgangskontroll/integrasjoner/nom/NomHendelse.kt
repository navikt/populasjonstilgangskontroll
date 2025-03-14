package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
    @JsonProperty("personident")
    val brukerId: BrukerId,
    @JsonProperty("navident")
    val ansattId: AnsattId,
    val startdato: LocalDate,
    val sluttdato: LocalDate?
)