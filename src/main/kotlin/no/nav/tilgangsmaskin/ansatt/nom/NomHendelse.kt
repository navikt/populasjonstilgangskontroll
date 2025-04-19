package no.nav.tilgangsmaskin.ansatt.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
        val personident: String,
        val navident: String,
        val startdato: LocalDate?,
        val sluttdato: LocalDate?
) {

    override fun toString() =
        "NomHendelse(personident=${personident.maskFnr()}, navident=$navident, startdato=$startdato, sluttdato=$sluttdato)"
}