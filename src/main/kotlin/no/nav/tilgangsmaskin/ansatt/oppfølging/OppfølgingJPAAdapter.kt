package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class OppfølgingJPAAdapter(private val repo: OppfølgingRepository) {

    private val log = getLogger(javaClass)

    fun enhetFor(id: String) =
        repo.findByBrukerid(id)?.kontor?.let(::Enhetsnummer)
            ?: repo.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)

    fun insert(id: UUID, brukerId: String, aktørId: String, start: Instant, kontor: String) =
        repo.save(OppfølgingEntity(id).apply {
            brukerid = brukerId
            aktoerid = aktørId
            startTidspunkt = start
            this.kontor = kontor
        }).also {
            log.info("Oppfølging opprettet for $id")
        }

    fun update(id: UUID, start: Instant, kontor: String) =
        repo.findById(id).orElseThrow()
            ?.let { entity ->
            entity.startTidspunkt = start
            entity.kontor = kontor
            repo.save(entity)
            log.info("Oppfølging oppdatert for $id")
        }

    fun delete(id: UUID) =
        repo.deleteById(id).also {
            log.info("Oppfølging avsluttet for $id")
        }
}