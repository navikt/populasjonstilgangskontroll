package no.nav.tilgangsmaskin.ansatt.oppfølging

import jakarta.persistence.EntityManager
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository,val entityManager: EntityManager) {

    private val log = getLogger(javaClass)

    fun avsluttOppfølging(id: UUID)  =
         repository.deleteByUUID(id).also {
            log.info("Oppfølging avsluttet for $id")
        }

    fun oppdaterKontor(id: UUID, brukerId: BrukerId, aktørId: AktørId, start: Instant, kontor: Enhetsnummer) =
         upsert(id,brukerId, aktørId, start, kontor).also {
            log.info("Oppfølging kontor endret til ${kontor.verdi} for $id")
        }

    fun startOppfølging(id: UUID, brukerId: BrukerId, aktørId: AktørId, start: Instant, kontor: Enhetsnummer) =
         upsert(id,brukerId, aktørId, start, kontor).also {
            log.info("Oppfølging registrert for $id")
        }

    fun enhetFor(id: String) =
        repository.findByBrukerid(id)?.kontor?.let(::Enhetsnummer) ?:
        repository.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)

    public fun upsert(id: UUID, brukerId: BrukerId, aktørId: AktørId, start: Instant, kontor: Enhetsnummer) =
        entityManager.createNativeQuery(UPSERT_QUERY)
            .setParameter("id", id)
            .setParameter("brukerid", brukerId.verdi)
            .setParameter("aktoerid", aktørId.verdi)
            .setParameter("startdato", start)
            .setParameter("kontor", kontor.verdi)
            .executeUpdate()

    companion object {
        private const val UPSERT_QUERY = """
            INSERT INTO OPPFOLGING (id, brukerid, aktoerid, start_tidspunkt, kontor, created, updated)
            VALUES (:id, :brukerid, :aktoerid, :startdato, :kontor,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (id)
            DO UPDATE SET
                kontor = EXCLUDED.kontor,
                start_tidspunkt = EXCLUDED.start_tidspunkt,
                updated = CURRENT_TIMESTAMP
        """
    }
}