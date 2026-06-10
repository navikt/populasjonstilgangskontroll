package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Component
class OppfølgingJPAAdapter(private val repo: OppfølgingRepository) {

    private val log = getLogger(javaClass)

    fun avslutt(id: UUID) =
        repo.deleteById(id).also {
            log.info("Oppfølging avsluttet for $id")
        }

    @Transactional(readOnly = true)
    fun enhetFor(id: String) =
        repo.findByBrukeridOrAktoerid(id, id)?.kontor?.let(::Enhetsnummer)

    fun registrer(id: UUID, brukerId: String, aktørId: String, start: Instant, kontor: String) =
        repo.upsert(id, brukerId, aktørId, start, kontor)
}