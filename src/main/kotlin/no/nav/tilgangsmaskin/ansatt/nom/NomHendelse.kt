package no.nav.tilgangsmaskin.ansatt.nom

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class NomHendelse(
        val personident: String,
        val navident: String,
        val startdato: LocalDate?,
        val sluttdato: LocalDate?) {

    override fun toString() =
        "${javaClass.simpleName} (personident=${personident.maskFnr()}, navident=$navident, startdato=$startdato, sluttdato=$sluttdato)"
}