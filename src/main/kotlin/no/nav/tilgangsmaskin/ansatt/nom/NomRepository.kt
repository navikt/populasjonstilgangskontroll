package no.nav.tilgangsmaskin.ansatt.nom

import io.micrometer.core.annotation.Timed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

@Timed
interface NomRepository : JpaRepository<NomEntity, Long> {
    @Query("SELECT n.fnr FROM NomEntity n WHERE n.navid = :navId AND  n.gyldigtil >= CURRENT_DATE")
    fun ansattFødselsnummer(navId: String): String?
    fun deleteByGyldigtilBefore(before: Instant = Instant.now()): Int
    fun findByNavid(navId: String): NomEntity?

    @Query(
        """
    INSERT INTO ansatte (navid, fnr, startdato, gyldigtil, created, updated)
    VALUES (:navid, :fnr, :startdato, :gyldigtil, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
    ON CONFLICT (navid)
    DO UPDATE SET
        fnr = EXCLUDED.fnr,
        startdato = EXCLUDED.startdato,
        gyldigtil = EXCLUDED.gyldigtil,
        updated = CURRENT_TIMESTAMP
"""
    )
    fun upsert(
        @Param("navid") navid: String,
        @Param("fnr") fnr: String,
        @Param("startdato") startdato: Instant,
        @Param("gyldigtil") gyldigtil: Instant
    ): NomEntity
}