package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import org.springframework.stereotype.Component
import java.time.Instant.now

@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() =
        repo.deleteByGyldigtilBefore(now())

    fun upsert(data: NomAnsattData) =
        with(data) {
            repo.upsert(ansattId.verdi, brukerId.verdi, gyldighet.start.toInstant(), gyldighet.endInclusive.toInstant())
        }

    fun fnrForAnsatt(ansattId: String) =
        repo.findFnrByNavidAndGyldigtilGreaterThanEqual(ansattId, now())?.let { BrukerId(it.fnr) }
}
