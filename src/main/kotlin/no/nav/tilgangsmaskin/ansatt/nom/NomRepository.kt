package no.nav.tilgangsmaskin.ansatt.nom

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.time.Instant.now

interface NomRepository : JpaRepository<NomEntity, Long> {
    fun findFnrByNavidAndGyldigtilGreaterThanEqual(navid: String, gyldigtil: Instant = now()): FnrProjection?
    fun deleteByGyldigtilBefore(before: Instant = now()): Int

    @Modifying
    @Query(value = """
        INSERT INTO Ansatte (navid, fnr, startdato, gyldigtil, created, updated)
        VALUES (:navid, :fnr, :startdato, :gyldigtil, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (navid)
        DO UPDATE SET
            fnr = EXCLUDED.fnr,
            startdato = EXCLUDED.startdato,
            gyldigtil = EXCLUDED.gyldigtil,
            updated = CURRENT_TIMESTAMP
    """, nativeQuery = true)
    fun upsert(
        @Param("navid") navid: String,
        @Param("fnr") fnr: String,
        @Param("startdato") startdato: Instant,
        @Param("gyldigtil") gyldigtil: Instant
    ): Int
}

interface FnrProjection {
    val fnr: String
}