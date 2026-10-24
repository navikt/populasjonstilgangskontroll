package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.ansatt.nom.Leder

data class EnkeltTilgangRapport(
    val html: String,
    val databaseIds: Set<Long>,
    val leder: Leder,
)
