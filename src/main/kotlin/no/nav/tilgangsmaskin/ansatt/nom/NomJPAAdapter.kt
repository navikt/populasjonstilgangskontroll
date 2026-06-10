package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() =
        repo.deleteByGyldigtilBefore()

    fun upsert(data: NomAnsattData) =
        with(data) {
            repo.upsert(ansattId.verdi, brukerId.verdi, gyldighet.start.toInstant(), gyldighet.endInclusive.toInstant())
        }

    @Transactional(readOnly = true)
    fun fnrForAnsatt(ansattId: String) =
        repo.findFnrByNavidAndGyldigtilGreaterThanEqual(ansattId)?.let { BrukerId(it.fnr) }
}
