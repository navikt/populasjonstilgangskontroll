package no.nav.tilgangsmaskin.ansatt.vergemål

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.bruker.BrukerId

@JsonIgnoreProperties(ignoreUnknown = true)
data class Vergemål(val vergehaver: BrukerId)

