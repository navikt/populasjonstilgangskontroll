package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component
class NomJPAAdapter(private val repository: NomRepository) {

    fun upsert(ansattId: String, fnr: String, slutt: LocalDate? = null) = repository.save(NomEntity(fnr,ansattId, slutt?.toInstant()))
    fun fnrForAnsatt(navId: String) = repository.findByNavid(navId)?.fnr


    fun LocalDate.toInstant(): Instant = atStartOfDay(ZoneId.systemDefault()).toInstant()}