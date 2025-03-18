package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
    val personident: String,
    val navident: String,
    val startdato: LocalDate?,
    val sluttdato: LocalDate?)  {

    override fun toString() = "NomHendelse(personident=${personident.mask()}, navident=$navident, startdato=$startdato, sluttdato=$sluttdato)"
}