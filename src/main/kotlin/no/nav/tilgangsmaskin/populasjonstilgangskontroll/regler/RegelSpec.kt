package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE


data class RegelSpec(val brukerId: BrukerId, val type: RegelType)


