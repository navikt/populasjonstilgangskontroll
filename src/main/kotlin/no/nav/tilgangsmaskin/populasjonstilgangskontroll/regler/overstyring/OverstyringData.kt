package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import java.time.LocalDate

data class OverstyringData(val begrunnelse: String, val varighet: LocalDate,val brukerId: BrukerId)
