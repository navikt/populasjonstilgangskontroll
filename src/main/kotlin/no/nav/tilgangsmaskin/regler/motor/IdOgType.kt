package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KOMPLETT_REGELTYPE


data class IdOgType(val brukerId: String, val type: RegelType = KOMPLETT_REGELTYPE)


