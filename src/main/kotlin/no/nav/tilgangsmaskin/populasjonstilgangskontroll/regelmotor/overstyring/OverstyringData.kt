package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import java.time.LocalDate

data class OverstyringData(val begrunnelse: String, val gyldigtil: LocalDate, val brukerId: BrukerId)
