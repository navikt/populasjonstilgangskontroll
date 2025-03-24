package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import java.time.LocalDate

data class OverstyringData(val brukerId: BrukerId, val begrunnelse: String, val gyldigtil: LocalDate)
