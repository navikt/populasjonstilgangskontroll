package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSett.RegelType.KJERNE_REGELTYPE


data class RegelSpec(val brukerId: BrukerId, val type: RegelType = KJERNE_REGELTYPE)


