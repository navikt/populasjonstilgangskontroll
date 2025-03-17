package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId.systemDefault

@Component
class NomJPAAdapter(private val repo: NomRepository) {

    fun upsert(ansattId: String, fnr: String, slutt: LocalDate? = null) =
         repo.save(repo.findByNavid(ansattId)?.apply {
            this.fnr = fnr
            gyldigtil = slutt?.toInstant()
        } ?: NomEntity(ansattId, fnr, slutt?.toInstant()))
    fun fnrForAnsatt(navId: String) = repo.finnGyldigFnr(navId)?.let { BrukerId(it.fnr) }
    private fun LocalDate.toInstant(): Instant = atStartOfDay(systemDefault()).toInstant()}