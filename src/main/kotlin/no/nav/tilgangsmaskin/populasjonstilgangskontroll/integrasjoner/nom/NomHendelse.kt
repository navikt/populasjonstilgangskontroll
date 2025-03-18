package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
    //@JsonProperty("personident")
    val personident: String,
    //@JsonProperty("navident")
    val navident: String,
    val startdato: LocalDate?,
    val sluttdato: LocalDate?
)