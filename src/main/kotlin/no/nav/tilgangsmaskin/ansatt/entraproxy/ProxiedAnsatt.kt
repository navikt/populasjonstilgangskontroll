package no.nav.tilgangsmaskin.ansatt.entraproxy

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProxiedAnsatt(
    val navIdent: AnsattId,
    val enhet: Enhet) {
    data class Enhet(
        val enhetnummer: Enhetsnummer,
        val navn: String
    )
}