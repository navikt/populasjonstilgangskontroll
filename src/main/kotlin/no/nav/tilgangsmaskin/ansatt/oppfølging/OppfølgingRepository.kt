package no.nav.tilgangsmaskin.ansatt.`oppfølging`

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface OppfølgingRepository : JpaRepository<OppfølgingEntity, UUID> {
    fun findByBrukeridOrAktoerid(brukerid: String, aktoerid: String): OppfølgingEntity?

    @Modifying(clearAutomatically = true)
    @Query(value = """
        INSERT INTO OPPFOLGING (id, brukerid, aktoerid, start_tidspunkt, kontor, created, updated)
        VALUES (:id, :brukerid, :aktoerid, :startdato, :kontor, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (id)
        DO UPDATE SET
            kontor = EXCLUDED.kontor,
            start_tidspunkt = EXCLUDED.start_tidspunkt,
            updated = CURRENT_TIMESTAMP
    """, nativeQuery = true)
    fun upsert(
        @Param("id") id: UUID,
        @Param("brukerid") brukerid: String,
        @Param("aktoerid") aktoerid: String,
        @Param("startdato") startdato: Instant,
        @Param("kontor") kontor: String
    ): Int
}
