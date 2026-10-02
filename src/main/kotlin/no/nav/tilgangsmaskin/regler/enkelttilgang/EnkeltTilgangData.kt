package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.tilgangsmaskin.bruker.BrukerId
import java.time.LocalDate
import java.time.LocalDate.now

data class EnkeltTilgangData(
    @field:Schema(description = "Fødselsnummer eller D-nummer", example = "22420094160")
    val brukerId: BrukerId,

    @field:Schema(description = "Begrunnelse for tilgang", example = "En begrunnelse")
    val begrunnelse: String,

    @field:Schema(description = "Tilgang gyldig til og med denne datoen", example = "2025-05-24")
    val gyldigtil: LocalDate = now().plusMonths(3)
)