package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

@Component
class NomJPAAdapter(private val repository: NomRepository) {

    fun fnrForAnsatt(ansattId: String, fnr: String) = repository.save(NomEntity(ansattId, fnr, Instant.now().plus(1, DAYS)))
    fun fnrForAnsatt(navId: String) = repository.findByNavid(navId)?.fnr
}