package no.nav.tilgangsmaskin.ansatt.nom

import java.time.LocalDate
import java.time.LocalDate.EPOCH
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode.Companion.ALWAYS
import no.nav.tilgangsmaskin.bruker.BrukerId

data class NomAnsattData(val ansattId: AnsattId, val brukerId: BrukerId, val gyldighet: NomAnsattPeriode = ALWAYS) {

    data class NomAnsattPeriode(override val start: LocalDate = EPOCH, override val endInclusive: LocalDate = FUTURE) :
        ClosedRange<LocalDate> {

        companion object {
            val FUTURE = LocalDate.now().plusYears(100)
            val ALWAYS = NomAnsattPeriode()
        }
    }
}