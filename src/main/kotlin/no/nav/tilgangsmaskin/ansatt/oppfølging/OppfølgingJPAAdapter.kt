package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class OppfølgingJPAAdapter(private val repo: OppfølgingRepository) {

    private val log = getLogger(javaClass)

    fun avslutt(id: UUID) =
        repo.deleteById(id).also {
            log.info("Oppfølging avsluttet for $id")
        }

    fun enhetFor(id: String) =
        repo.findByBrukeridOrAktoerid(id, id)?.let { Enhetsnummer(it.kontor) }

    fun registrer(id: UUID, brukerId: String, aktørId: String, start: Instant, kontor: String) =
        repo.upsert(id, brukerId, aktørId, start, kontor)
}