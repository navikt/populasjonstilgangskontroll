package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomAnsattData.NomAnsattPeriode.Companion.FOREVER
import java.time.LocalDate
import java.time.LocalDate.EPOCH

data class NomAnsattData(val ansattId: AnsattId, val brukerId: BrukerId, val gyldighet: NomAnsattPeriode = NomAnsattPeriode(FOREVER)) {
    data class NomAnsattPeriode(override val start: LocalDate, override val endInclusive: LocalDate) : ClosedRange<LocalDate> {
        constructor(endInclusive: LocalDate) : this(EPOCH, endInclusive)
        companion object {
            val FOREVER = LocalDate.now().plusYears(100)
        }
    }
}