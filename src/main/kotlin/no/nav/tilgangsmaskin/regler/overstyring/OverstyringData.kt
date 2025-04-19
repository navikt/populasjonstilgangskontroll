package no.nav.tilgangsmaskin.regler.overstyring

import java.time.LocalDate
import no.nav.tilgangsmaskin.bruker.BrukerId

data class OverstyringData(val brukerId: BrukerId, val begrunnelse: String, val gyldigtil: LocalDate)
