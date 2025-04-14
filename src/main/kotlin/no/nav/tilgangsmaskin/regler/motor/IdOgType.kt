package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType.KJERNE_REGELTYPE


data class IdOgType(val brukerId: String, val type: RegelType = KJERNE_REGELTYPE)


