package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId


data class RegelSpec(val brukerId: BrukerId, val type: RegelType)


