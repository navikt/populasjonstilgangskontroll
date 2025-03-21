package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import java.time.LocalDate
import java.time.LocalDate.EPOCH

data class NomAnsattPeriode(override val start: LocalDate, override val endInclusive: LocalDate) : ClosedRange<LocalDate> {
    constructor(endInclusive: LocalDate) : this(EPOCH, endInclusive)
}