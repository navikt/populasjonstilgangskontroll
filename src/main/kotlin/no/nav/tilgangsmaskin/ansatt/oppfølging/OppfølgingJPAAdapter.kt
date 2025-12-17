package no.nav.tilgangsmaskin.ansatt.oppfølging

import jakarta.persistence.EntityManager
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class OppfølgingJPAAdapter(private val repository: OppfølgingRepository,val entityManager: EntityManager) {

    fun avsluttOppfølging(id: UUID) =
        repository.deleteByUUID(id)

    fun oppdaterKontor(id: UUID,brukerId: BrukerId, aktørId: AktørId, start: Instant, enhetsnummer: Enhetsnummer) =
        upsert(id,brukerId, aktørId, start, enhetsnummer)
    /*
    fun oppdaterKontor(id: UUID, enhetsnummer: Enhetsnummer) =
        repository.updateKontorById(id,enhetsnummer.verdi)
*/
    fun startOppfølging(id: UUID,brukerId: BrukerId, aktørId: AktørId, start: Instant, enhetsnummer: Enhetsnummer) =
        upsert(id,brukerId, aktørId, start, enhetsnummer)
        /*repository.save(OppfølgingEntity(id).apply {
            brukerid = brukerId.verdi
            aktoerid = aktørId.verdi
            startTidspunkt = start
            kontor = enhetsnummer.verdi
        })*/

    private fun upsert(id: UUID,brukerId: BrukerId, aktørId: AktørId, start: Instant, enhetsnummer: Enhetsnummer) =
        entityManager.createNativeQuery(UPSERT_QUERY)
            .setParameter("id", id)
            .setParameter("brukerid", brukerId.verdi)
            .setParameter("aktoerid", aktørId.verdi)
            .setParameter("startdato", start)
            .setParameter("kontor", enhetsnummer.verdi)
            .executeUpdate()


    fun enhetFor(id: String) =
        repository.findByBrukerid(id)?.kontor?.let(::Enhetsnummer) ?:
        repository.findByAktoerid(id)?.kontor?.let(::Enhetsnummer)

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