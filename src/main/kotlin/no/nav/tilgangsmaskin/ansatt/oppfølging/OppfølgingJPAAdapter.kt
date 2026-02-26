package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class OppfølgingJPAAdapter(private val repo: OppfølgingRepository) {

    fun enhetFor(id: String) =
        repo.findByBrukerid(id)?.kontor?.let(::Enhetsnummer)
            ?: repo.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)

    fun insert(id: UUID, brukerId: String, aktørId: String, start: Instant, kontor: String) =
        repo.save(OppfølgingEntity(id).apply {
            brukerid = brukerId
            aktoerid = aktørId
            startTidspunkt = start
            this.kontor = kontor
        })

    fun update(id: UUID, start: Instant, kontor: String) =
        repo.findById(id).orElse(null)
            ?.let { entity ->
                entity.startTidspunkt = start
                entity.kontor = kontor
                repo.save(entity)
            }

    fun delete(id: UUID) =
        repo.deleteById(id)
}