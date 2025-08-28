package no.nav.tilgangsmaskin.regler.overstyring

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.tilgangsmaskin.bruker.BrukerId
import java.time.LocalDate

@Schema(requiredProperties = ["brukerId","begrunnelse","gyldigTil"], example = """
  {
  "brukerId": "22420094160",
  "begrunnelse": "En begrunnelse",
  "gyldigtil": "2025-05-24"
}
""")
data class OverstyringData(val brukerId: BrukerId, val begrunnelse: String,  val gyldigtil: LocalDate = LocalDate.now().plusMonths(3))
