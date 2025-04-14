package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.toInstant
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() = repo.deleteByGyldigtilBefore()

    fun upsert(data: NomAnsattData) =
        with(data) {
            upsert(ansattId, brukerId, gyldighet.start.toInstant(), gyldighet.endInclusive.toInstant())
        }

    private fun upsert(ansattId: AnsattId, ansattFnr: BrukerId, start: Instant, slutt: Instant) =
        repo.upsert(ansattId.verdi, ansattFnr.verdi, start, slutt)
    /*
     repo.save(repo.findByNavid(ansattId.verdi)?.apply {
         this.fnr = fnr
         updated = now()
         startdato = start.toInstant()
         gyldigtil = slutt.toInstant()
     } ?: NomEntity(ansattId.verdi, ansattFnr.verdi, start.toInstant(), slutt.toInstant())).id!! */

    fun fnrForAnsatt(ansattId: String) = repo.ansattFødselsnummer(ansattId)?.let { BrukerId(it) }

}

