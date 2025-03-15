package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component
class NomJPAAdapter(private val repo: NomRepository) {
    fun upsert(ansattId: String, fnr: String, slutt: LocalDate? = null) = repo.save(repo.findByNavid(ansattId)?.copy(fnr = fnr, gyldigtil = slutt?.toInstant()) ?: NomEntity(ansattId, fnr, slutt?.toInstant()))
    fun fnrForAnsatt(navId: String) = repo.findByNavid(navId)?.fnr
    fun LocalDate.toInstant(): Instant = atStartOfDay(ZoneId.systemDefault()).toInstant()}