package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.ZoneId.systemDefault
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId as AnsattFnr
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomHendelseKonsument.NomAnsattData


@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() = repo.deleteByGyldigtilBefore(now())

    fun upsert(data: NomAnsattData) =
        upsert(data.ansattId, data.brukerId, data.gyldighet.start, data.gyldighet.endInclusive)

    private fun upsert(ansattId: AnsattId, ansattFnr: BrukerId, start: LocalDate,slutt: LocalDate) =
         repo.save(repo.findByNavid(ansattId.verdi)?.apply {
             this.fnr = fnr
             startdato = start.toInstant()
             gyldigtil = slutt.toInstant()
        } ?: NomEntity(ansattId.verdi, ansattFnr.verdi, start.toInstant(),slutt.toInstant())).id!!
    fun fnrForAnsatt(ansattId: String) = repo.ansattFødselsnummer(ansattId)?.let { AnsattFnr(it) }
    private fun LocalDate.toInstant(): Instant = atStartOfDay(systemDefault()).toInstant()}