package no.nav.tilgangsmaskin.regler.motor

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE



@Schema(requiredProperties = ["brukerId"],  description = "Sett av identifikatorer og regelsett", example = """
  {
    "brukerId": "22420094160",
    "type": "KOMPLETT_REGELTYPE"
  }
""")
data class IdOgType(val brukerId: String, val type: RegelType = KOMPLETT_REGELTYPE)


