package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDate
import java.time.ZoneId.systemDefault
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId as AnsattFnr

@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun ryddOpp() = repo.deleteByGyldigtilBefore(now())

    fun upsert(ansattId: String, ansattFnr: String, slutt: LocalDate? = null) =
         repo.save(repo.findByNavid(ansattId)?.apply {
            this.fnr = fnr
            gyldigtil = slutt?.toInstant()
        } ?: NomEntity(ansattId, ansattFnr, slutt?.toInstant()))
    fun fnrForAnsatt(ansattId: String) = repo.finnGyldigAnsattFnr(ansattId)?.let { AnsattFnr(it) }
    private fun LocalDate.toInstant(): Instant = atStartOfDay(systemDefault()).toInstant()}