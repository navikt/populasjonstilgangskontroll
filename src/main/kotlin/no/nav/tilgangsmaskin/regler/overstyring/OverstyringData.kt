package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.bruker.BrukerId
import java.time.LocalDate

data class OverstyringData(val brukerId: BrukerId, val begrunnelse: String, val gyldigtil: LocalDate)
