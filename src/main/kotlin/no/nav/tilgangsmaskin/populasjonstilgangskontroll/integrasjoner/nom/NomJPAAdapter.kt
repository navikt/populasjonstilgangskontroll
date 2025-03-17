package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId as AnsattFnr
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId.systemDefault

@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun upsert(ansattId: String, ansattFnr: String, slutt: LocalDate? = null) =
         repo.save(repo.findByNavid(ansattId)?.apply {
            this.fnr = fnr
            gyldigtil = slutt?.toInstant()
        } ?: NomEntity(ansattId, ansattFnr, slutt?.toInstant()))
    fun fnrForAnsatt(ansattId: String) = repo.finnGyldigAnsattFnr(ansattId)?.let { AnsattFnr(it) }
    private fun LocalDate.toInstant(): Instant = atStartOfDay(systemDefault()).toInstant()}