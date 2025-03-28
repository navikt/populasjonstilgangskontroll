package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.TimeExtensions.toInstant
import org.springframework.stereotype.Component
import java.time.Instant.now
import java.time.LocalDate
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId as AnsattFnr


@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() = repo.deleteByGyldigtilBefore()

    fun upsert(data: NomAnsattData) =
       with(data)  {
           upsert(ansattId, brukerId, gyldighet.start, gyldighet.endInclusive)
       }

    private fun upsert(ansattId: AnsattId, ansattFnr: BrukerId, start: LocalDate, slutt: LocalDate) =
         repo.save(repo.findByNavid(ansattId.verdi)?.apply {
             this.fnr = fnr
             updated = now()
             startdato = start.toInstant()
             gyldigtil = slutt.toInstant()
        } ?: NomEntity(ansattId.verdi, ansattFnr.verdi, start.toInstant(),slutt.toInstant())).id!!

    fun fnrForAnsatt(ansattId: String) = repo.ansattFødselsnummer(ansattId)?.let { AnsattFnr(it) }

}

