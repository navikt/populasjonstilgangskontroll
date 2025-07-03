package no.nav.tilgangsmaskin.regler.overstyring

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.ValidOverstyring
import java.time.LocalDate

@Schema(requiredProperties = ["brukerId","begrunnelse","gyldigTil"], example = """
  {
  "brukerId": "22420094160",
  "begrunnelse": "En begrunnelse",
  "gyldigtil": "2025-05-24"
}
""")
data class OverstyringData(val brukerId: BrukerId, @Size(min = 10, max = 255) val begrunnelse: String, @ValidOverstyring val gyldigtil: LocalDate = LocalDate.now().plusMonths(3))
