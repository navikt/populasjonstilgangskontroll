package no.nav.tilgangsmaskin.ansatt.nom

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface NomRepository : JpaRepository<NomEntity, Long> {
    fun findFnrByNavidAndGyldigtilGreaterThanEqual(navid: String, gyldigtil: Instant): Fnr?
    fun deleteByGyldigtilBefore(before: Instant): Int

    @Query(value = """
        INSERT INTO Ansatte (navid, fnr, startdato, gyldigtil, created, updated)
        VALUES (:navid, :fnr, :startdato, :gyldigtil, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (navid)
        DO UPDATE SET
            fnr = EXCLUDED.fnr,
            startdato = EXCLUDED.startdato,
            gyldigtil = EXCLUDED.gyldigtil,
            updated = CURRENT_TIMESTAMP
        RETURNING id
    """, nativeQuery = true)
    fun upsert(
        @Param("navid") navid: String,
        @Param("fnr") fnr: String,
        @Param("startdato") startdato: Instant,
        @Param("gyldigtil") gyldigtil: Instant
    ): Long
}

interface Fnr {
    val fnr: String
}