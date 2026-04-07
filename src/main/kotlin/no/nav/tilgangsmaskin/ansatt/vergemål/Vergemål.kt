package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.bruker.BrukerId

data class Vergemål(
    val vergehaver: BrukerId,
    val verge: BrukerId,
    val leserettigheter: List<String>,
    val skriverettigheter: List<String>
)

